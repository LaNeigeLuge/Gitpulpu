package com.jetpackduba.gitnuro.data.git.stash

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.data.mappers.JGitCommitMapper
import com.jetpackduba.gitnuro.data.mappers.JGitIdentityMapper
import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GenericError
import com.jetpackduba.gitnuro.domain.exceptions.UncommittedChangesDetectedException
import com.jetpackduba.gitnuro.domain.models.Commit
import kotlinx.coroutines.runBlocking
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * JGit reports "would overwrite local changes" and "applied with conflicts" as the same exception.
 * These tests pin the distinction the UI depends on: only the second one leaves something to resolve.
 */
class PopStashGitActionTest {

    private lateinit var repoDir: File
    private lateinit var git: Git
    private val jgit = JGit()
    private val mapper = JGitCommitMapper(JGitIdentityMapper())
    private val file = "file.txt"

    private val popStash by lazy { PopStashGitAction(ApplyStashGitAction(jgit), DeleteStashGitAction(jgit), jgit) }

    @BeforeEach
    fun setUp() {
        repoDir = File.createTempFile("gitpulpu-stash-test", "").let { it.delete(); it.mkdirs(); it }
        git = Git.init().setDirectory(repoDir).call()

        write(file, "line1\nshared\nline3\n")
        git.add().addFilepattern(file).call()
        git.commit().setMessage("base").call()
    }

    @AfterEach
    fun tearDown() {
        git.close()
        repoDir.deleteRecursively()
    }

    private fun write(name: String, content: String) = File(repoDir, name).writeText(content)
    private fun path() = repoDir.absolutePath
    private fun stashList(): List<Commit> = git.stashList().call().map { mapper.toDomain(it) }

    private fun stash(content: String): Commit {
        write(file, content)
        return mapper.toDomain(git.stashCreate().call())
    }

    @Test
    fun `pops a clean stash and drops it`() = runBlocking {
        val stash = stash("line1\nSTASHED\nline3\n")

        val result = popStash(path(), stash)

        assertEquals(false, (result as Either.Ok).value)
        assertTrue(File(repoDir, file).readText().contains("STASHED"))
        assertTrue(stashList().isEmpty())
    }

    @Test
    fun `keeps the stash and reports conflicts when the apply conflicts`() = runBlocking {
        val stash = stash("line1\nSTASHED\nline3\n")
        write(file, "line1\nCOMMITTED\nline3\n")
        git.add().addFilepattern(file).call()
        git.commit().setMessage("second").call()

        val result = popStash(path(), stash)

        assertEquals(true, (result as Either.Ok).value)
        assertEquals(listOf(file), git.status().call().conflicting.toList())
        assertTrue(File(repoDir, file).readText().contains("<<<<<<<"))
        assertEquals(listOf(stash.hash), stashList().map { it.hash })
    }

    @Test
    fun `fails with an actionable error when local changes would be overwritten`() = runBlocking {
        val stash = stash("line1\nSTASHED\nline3\n")
        write(file, "line1\nLOCAL EDIT\nline3\n")

        val result = popStash(path(), stash)

        val error = (result as Either.Err).error as GenericError
        assertTrue(error.exception is UncommittedChangesDetectedException, "got: ${error.exception}")
        assertTrue(error.message.contains("overwritten"), error.message)
        // Nothing applied, nothing lost, nothing to resolve.
        assertTrue(git.status().call().conflicting.isEmpty())
        assertTrue(File(repoDir, file).readText().contains("LOCAL EDIT"))
        assertEquals(listOf(stash.hash), stashList().map { it.hash })
        assertFalse(File(repoDir, file).readText().contains("STASHED"))
    }
}
