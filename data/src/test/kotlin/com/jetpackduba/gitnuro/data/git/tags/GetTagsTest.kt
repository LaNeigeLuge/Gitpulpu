package com.jetpackduba.gitnuro.data.git.tags

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.errors.Either
import kotlinx.coroutines.runBlocking
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/** Both lightweight and annotated tags must resolve commitHash to the underlying commit. */
class GetTagsTest {

    private lateinit var repoDir: File
    private lateinit var git: Git
    private val action = GetTagsGitAction(JGit())

    @BeforeEach
    fun setUp() {
        repoDir = File.createTempFile("gitpulpu-tags-test", "").let { it.delete(); it.mkdirs(); it }
        git = Git.init().setDirectory(repoDir).call()
        File(repoDir, "f.txt").writeText("x\n")
        git.add().addFilepattern(".").call()
        git.commit().setMessage("c1").setAuthor("t", "t@t").setCommitter("t", "t@t").call()
    }

    @AfterEach
    fun tearDown() { git.close(); repoDir.deleteRecursively() }

    @Test
    fun `lightweight and annotated tags both map to the commit hash`() = runBlocking {
        val commitHash = git.repository.resolve("HEAD").name

        git.tag().setName("v1.0-light").call()                       // lightweight
        git.tag().setName("v1.0-annot").setAnnotated(true).setMessage("release").call() // annotated

        val tags = (action(repoDir.absolutePath) as Either.Ok).value
        assertEquals(2, tags.size)
        tags.forEach { tag ->
            assertEquals(commitHash, tag.commitHash, "${tag.simpleName} should resolve to the commit")
        }
        Unit
    }
}
