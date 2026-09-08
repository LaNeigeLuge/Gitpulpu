package com.jetpackduba.gitnuro.data.git.remote_operations

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.data.git.branches.GetTrackingBranchGitAction
import com.jetpackduba.gitnuro.data.git.branches.SetTrackingBranchGitAction
import com.jetpackduba.gitnuro.domain.models.Tag
import com.jetpackduba.gitnuro.domain.credentials.CredentialsHandler
import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError
import com.jetpackduba.gitnuro.domain.interfaces.IHandleTransportGitAction
import kotlinx.coroutines.runBlocking
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.Transport
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * The Push button pushes tags along with the branch, so a tag created in the app reaches the remote
 * without a separate action. Force pushes are the exception: forcing every refspec would let a
 * branch force-push rewrite remote tags too.
 */
class PushTagsTest {

    private lateinit var localDir: File
    private lateinit var remoteDir: File
    private lateinit var git: Git
    private lateinit var remote: Git
    private val jgit = JGit()

    /** Local file transport needs no credentials, so the handler is a pass-through. */
    private val transport = object : IHandleTransportGitAction {
        override suspend fun <R> invoke(
            repositoryPath: String?,
            block: suspend CredentialsHandler.() -> R,
        ): Either<R, GitError> = Either.Ok(
            block(object : CredentialsHandler {
                override fun handleTransport(transport: Transport?) = Unit
            })
        )
    }

    private val pushTag = PushTagGitAction(
        transport,
        GetTrackingBranchGitAction(jgit),
        jgit,
    )

    private val pushBranch = PushBranchGitAction(
        transport,
        GetTrackingBranchGitAction(jgit),
        SetTrackingBranchGitAction(jgit),
        jgit,
    )

    @BeforeEach
    fun setUp() {
        remoteDir = tempDir("gitpulpu-push-remote")
        remote = Git.init().setBare(true).setDirectory(remoteDir).call()

        localDir = tempDir("gitpulpu-push-local")
        git = Git.init().setDirectory(localDir).call()
        git.remoteAdd().setName("origin").setUri(org.eclipse.jgit.transport.URIish(remoteDir.absolutePath)).call()

        File(localDir, "f.txt").writeText("hello\n")
        git.add().addFilepattern(".").call()
        git.commit().setMessage("first").setAuthor("t", "t@t").setCommitter("t", "t@t").call()
        git.tag().setName("v1.0.0").setMessage("release").call()
    }

    @AfterEach
    fun tearDown() {
        git.close()
        remote.close()
        localDir.deleteRecursively()
        remoteDir.deleteRecursively()
    }

    private fun tempDir(prefix: String) = File.createTempFile(prefix, "").let {
        it.delete(); it.mkdirs(); it
    }

    private fun remoteTag() = remote.repository.refDatabase.findRef("refs/tags/v1.0.0")

    @Test
    fun `pushing one tag sends only that tag`() = runBlocking {
        git.tag().setName("v0.9.0").setMessage("older").call()

        val result = pushTag(localDir.absolutePath, Tag("", "", "refs/tags/v1.0.0"))

        assertTrue(result is Either.Ok, "push should succeed: $result")
        assertNotNull(remoteTag(), "the requested tag should be on the remote")
        assertNull(
            remote.repository.refDatabase.findRef("refs/tags/v0.9.0"),
            "the other local tag must stay local",
        )
    }

    @Test
    fun `pushing one tag does not push the branch`() = runBlocking {
        pushTag(localDir.absolutePath, Tag("", "", "refs/tags/v1.0.0"))

        assertNotNull(remoteTag())
        assertNull(remote.repository.refDatabase.findRef("refs/heads/" + git.repository.branch))
    }

    @Test
    fun `pushing with tags sends the tag to the remote`() = runBlocking {
        val result = pushBranch(localDir.absolutePath, force = false, pushTags = true, pushWithLease = false, specificBranch = null)

        assertTrue(result is Either.Ok, "push should succeed: $result")
        assertNotNull(remoteTag(), "the tag should exist on the remote")
    }

    @Test
    fun `pushing without tags leaves the tag local`() = runBlocking {
        pushBranch(localDir.absolutePath, force = false, pushTags = false, pushWithLease = false, specificBranch = null)

        assertNull(remoteTag(), "the tag should not have been pushed")
    }

    @Test
    fun `force push never touches remote tags`() = runBlocking {
        pushBranch(localDir.absolutePath, force = true, pushTags = true, pushWithLease = false, specificBranch = null)

        assertNull(remoteTag(), "a force push must not force-update tags")
    }
}
