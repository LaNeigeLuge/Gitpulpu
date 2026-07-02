package com.jetpackduba.gitnuro.data.git.undo

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.errors.Either
import kotlinx.coroutines.runBlocking
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * A commit made on a detached HEAD (directly on top of a branch) can be moved onto that branch:
 * the branch fast-forwards to the commit, HEAD reattaches, and the commit's SHA is preserved.
 */
class MoveCommitToBranchTest {

    private lateinit var repoDir: File
    private lateinit var git: Git
    private val jgit = JGit()
    private val move = MoveCommitToBranchGitAction(jgit)

    @BeforeEach
    fun setUp() {
        repoDir = File.createTempFile("gitpulpu-move-test", "").let { it.delete(); it.mkdirs(); it }
        git = Git.init().setDirectory(repoDir).call()
        commit("base", "b\n")   // feature will sit here
        git.branchCreate().setName("feature").call()
    }

    @AfterEach
    fun tearDown() { git.close(); repoDir.deleteRecursively() }

    @Test
    fun `moves a detached commit onto the branch by fast-forward, preserving the hash`() = runBlocking {
        // Detach at feature's tip, then commit on top (orphaned)
        val featureTipBefore = git.repository.resolve("feature").name
        git.checkout().setName(featureTipBefore).call()   // detached HEAD
        commit("work", "w\n")
        val orphanSha = git.repository.resolve("HEAD").name
        assertTrue(orphanSha != featureTipBefore)

        val res = move(repoDir.absolutePath, "feature")
        assertTrue(res is Either.Ok, "move should succeed: $res")

        // feature now points at the moved commit (same SHA — a real move, not a copy)
        assertEquals(orphanSha, git.repository.resolve("feature").name)
        // HEAD is reattached to the branch
        assertEquals("refs/heads/feature", git.repository.fullBranch)
        Unit
    }

    private fun commit(message: String, content: String) {
        File(repoDir, "f.txt").appendText(content)
        git.add().addFilepattern(".").call()
        git.commit().setMessage(message).setAuthor("t", "t@t").setCommitter("t", "t@t").call()
    }
}
