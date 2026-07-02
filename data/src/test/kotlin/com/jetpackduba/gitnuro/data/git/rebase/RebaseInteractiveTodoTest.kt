package com.jetpackduba.gitnuro.data.git.rebase

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.data.mappers.JGitRebaseTodoLineMapper
import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.models.RebaseAction
import kotlinx.coroutines.runBlocking
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Verifies that starting an interactive rebase writes a todo list, and that the todo-line
 * reader parses it into editable domain lines (the data the editor renders).
 */
class RebaseInteractiveTodoTest {

    private lateinit var repoDir: File
    private lateinit var git: Git
    private val jgit = JGit()

    @BeforeEach
    fun setUp() {
        repoDir = File.createTempFile("gitpulpu-rebase-test", "").let {
            it.delete(); it.mkdirs(); it
        }
        git = Git.init().setDirectory(repoDir).call()

        // Three commits on top of a base, so an interactive rebase onto the base lists them
        commit("base", "b\n")
        commit("first", "1\n")
        commit("second", "2\n")
        commit("third", "3\n")
    }

    @AfterEach
    fun tearDown() {
        git.close()
        repoDir.deleteRecursively()
    }

    @Test
    fun `starting interactive rebase produces editable todo lines`() = runBlocking {
        val base = git.log().call().toList().last { it.fullMessage.trim() == "base" }

        val start = StartRebaseInteractiveGitAction(jgit)
        val startResult = start(
            repoDir.absolutePath,
            com.jetpackduba.gitnuro.domain.models.Commit(
                hash = base.name,
                message = "base",
                committer = com.jetpackduba.gitnuro.domain.models.Identity("t", "t@t"),
                author = com.jetpackduba.gitnuro.domain.models.Identity("t", "t@t"),
                date = 0,
                parentsHashes = emptyList(),
            ),
        )
        assertTrue(startResult is Either.Ok, "start rebase should succeed: $startResult")

        val read = GetRebaseInteractiveTodoLinesGitAction(jgit, JGitRebaseTodoLineMapper())
        val lines = (read(repoDir.absolutePath) as Either.Ok).value

        // Three pickable commits, in order first -> second -> third
        val picks = lines.filter { it.action == RebaseAction.PICK }
        assertEquals(3, picks.size)
        assertEquals(listOf("first", "second", "third"), picks.map { it.shortMessage })

        git.rebase().setOperation(org.eclipse.jgit.api.RebaseCommand.Operation.ABORT).call()
        Unit
    }

    private fun commit(message: String, content: String) {
        File(repoDir, "f.txt").appendText(content)
        git.add().addFilepattern(".").call()
        git.commit().setMessage(message).setAuthor("t", "t@t").setCommitter("t", "t@t").call()
    }
}
