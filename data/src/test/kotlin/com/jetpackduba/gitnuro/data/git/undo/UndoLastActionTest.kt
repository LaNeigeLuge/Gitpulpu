package com.jetpackduba.gitnuro.data.git.undo

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.models.UndoableAction
import kotlinx.coroutines.runBlocking
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/** Real-git test: each undoable action, once reversed, restores the prior state. */
class UndoLastActionTest {

    private lateinit var repoDir: File
    private lateinit var git: Git
    private val jgit = JGit()
    private val undo = UndoLastActionGitAction(jgit)

    @BeforeEach
    fun setUp() {
        repoDir = File.createTempFile("gitpulpu-undo-test", "").let { it.delete(); it.mkdirs(); it }
        git = Git.init().setDirectory(repoDir).call()
        commit("c1", "1\n")
        // second branch off master
        git.branchCreate().setName("feature").call()
        commit("c2", "2\n")
    }

    @AfterEach
    fun tearDown() {
        git.close(); repoDir.deleteRecursively()
    }

    private fun path() = repoDir.absolutePath

    @Test
    fun `undo checkout returns to the previous branch`() = runBlocking {
        val original = git.repository.fullBranch          // refs/heads/master
        git.checkout().setName("feature").call()
        assertEquals("refs/heads/feature", git.repository.fullBranch)

        val res = undo(path(), UndoableAction.Checkout(original, "Checkout feature"))
        assertTrue(res is Either.Ok)
        assertEquals(original, git.repository.fullBranch)
    }

    @Test
    fun `undo reset restores the branch tip`() = runBlocking {
        val tipBeforeReset = git.repository.resolve("HEAD").name
        val branch = git.repository.branch

        // Hard reset back one commit
        git.reset().setMode(org.eclipse.jgit.api.ResetCommand.ResetType.HARD).setRef("HEAD~1").call()
        assertTrue(git.repository.resolve("HEAD").name != tipBeforeReset)

        val res = undo(path(), UndoableAction.ResetBranch(branch, tipBeforeReset, "Reset $branch"))
        assertTrue(res is Either.Ok)
        assertEquals(tipBeforeReset, git.repository.resolve("HEAD").name)
    }

    @Test
    fun `undo delete branch recreates it at the same commit`() = runBlocking {
        val featureTip = git.repository.resolve("feature").name
        git.branchDelete().setBranchNames("feature").setForce(true).call()
        assertTrue(git.repository.resolve("feature") == null)

        val res = undo(path(), UndoableAction.DeleteBranch("feature", featureTip, "Delete feature"))
        assertTrue(res is Either.Ok)

        val restored = git.repository.resolve("feature")
        assertNotNull(restored, "branch should be recreated")
        assertEquals(featureTip, restored.name)
    }

    private fun commit(message: String, content: String) {
        File(repoDir, "f.txt").appendText(content)
        git.add().addFilepattern(".").call()
        git.commit().setMessage(message).setAuthor("t", "t@t").setCommitter("t", "t@t").call()
    }
}
