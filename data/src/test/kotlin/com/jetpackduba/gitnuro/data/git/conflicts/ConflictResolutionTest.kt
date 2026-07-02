package com.jetpackduba.gitnuro.data.git.conflicts

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.ConflictParser
import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.models.ConflictChoice
import kotlinx.coroutines.runBlocking
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeResult
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Real-git integration test: builds a repo, forces a merge conflict, and verifies the
 * conflict-reading + resolution actions produce a clean, staged, conflict-free result.
 */
class ConflictResolutionTest {

    private lateinit var repoDir: File
    private lateinit var git: Git
    private val jgit = JGit()

    private val file = "file.txt"

    @BeforeEach
    fun setUp() {
        repoDir = File.createTempFile("gitpulpu-conflict-test", "").let {
            it.delete(); it.mkdirs(); it
        }
        git = Git.init().setDirectory(repoDir).call()

        // Base commit
        write(file, "line1\nshared\nline3\n")
        commit("base")

        // main branch changes the middle line one way
        write(file, "line1\nMAIN VERSION\nline3\n")
        commit("main change")

        // feature branch (from base) changes it another way
        git.checkout().setName("feature").setCreateBranch(true)
            .setStartPoint("HEAD~1").call()
        write(file, "line1\nFEATURE VERSION\nline3\n")
        commit("feature change")

        // Merge main into feature -> conflict on the middle line
        val master = git.repository.branch.let { "master" }
        val mergeResult = git.merge()
            .include(git.repository.resolve("master"))
            .call()

        assertEquals(MergeResult.MergeStatus.CONFLICTING, mergeResult.mergeStatus)
    }

    @AfterEach
    fun tearDown() {
        git.close()
        repoDir.deleteRecursively()
    }

    private fun path() = repoDir.absolutePath

    @Test
    fun `reads a real conflict into ours and theirs`() = runBlocking {
        val result = GetConflictedFileGitAction(jgit)(path(), file)
        val conflicted = (result as Either.Ok).value

        assertEquals(1, conflicted.conflictCount)
        // On the feature branch, "ours" is the feature version, "theirs" is master
        val resolvedOurs = ConflictParser.resolve(conflicted, mapOf(0 to ConflictChoice.OURS))
        assertTrue(resolvedOurs.contains("FEATURE VERSION"))
        assertFalse(resolvedOurs.contains("<<<<<<<"))
    }

    @Test
    fun `resolving with content writes and stages a clean file`() = runBlocking {
        val resolve = ResolveConflictGitAction(jgit)
        val getConflict = GetConflictedFileGitAction(jgit)

        val conflicted = (getConflict(path(), file) as Either.Ok).value
        val resolved = ConflictParser.resolve(conflicted, mapOf(0 to ConflictChoice.THEIRS))

        val res = resolve(path(), file, resolved)
        assertTrue(res is Either.Ok)

        // File on disk is clean and has the chosen side
        val onDisk = File(repoDir, file).readText()
        assertFalse(ConflictParser.hasConflictMarkers(onDisk))
        assertTrue(onDisk.contains("MAIN VERSION"))

        // No more conflicting entries in git status
        val status = git.status().call()
        assertTrue(status.conflicting.isEmpty(), "expected no conflicting files after resolve")
    }

    private fun write(name: String, content: String) {
        File(repoDir, name).writeText(content)
    }

    private fun commit(message: String) {
        git.add().addFilepattern(".").call()
        git.commit().setMessage(message).setAuthor("t", "t@t").setCommitter("t", "t@t").call()
    }
}
