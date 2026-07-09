package com.jetpackduba.gitnuro.domain.usecases

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ScanRepositoriesUseCaseTest {

    private val scan = ScanRepositoriesUseCase()

    private fun File.mkRepo() = File(this, ".git").mkdirs()
    private fun File.mkDir(name: String) = File(this, name).apply { mkdirs() }

    @Test
    fun `finds real repos and ignores noise`(@TempDir root: File) = runBlocking {
        // real projects
        val projectA = root.mkDir("projectA").apply { mkRepo() }
        val projectB = root.mkDir("group/projectB").apply { mkRepo() }

        // noise that must be excluded
        root.mkDir(".hidden/secretRepo").apply { mkRepo() }        // hidden dir
        root.mkDir("app/node_modules/dep").apply { mkRepo() }      // dependency dir
        root.mkDir("plainDir")                                      // not a repo

        // submodule inside projectA must NOT be listed separately
        File(projectA, "sub").apply { mkdirs(); mkRepo() }

        val result = scan(roots = listOf(root.absolutePath), maxDepth = 5)

        assertEquals(
            listOf(projectA.absolutePath, projectB.absolutePath).sorted(),
            result,
        )
    }

    @Test
    fun `respects max depth`(@TempDir root: File) = runBlocking {
        root.mkDir("a/b/c/deepRepo").apply { mkRepo() } // repo at depth 4

        assertEquals(emptyList<String>(), scan(roots = listOf(root.absolutePath), maxDepth = 2))
        assertEquals(1, scan(roots = listOf(root.absolutePath), maxDepth = 5).size)
    }

    @Test
    fun `root that is itself a repo is returned`(@TempDir root: File) = runBlocking {
        root.mkRepo()

        assertEquals(listOf(root.absolutePath), scan(roots = listOf(root.absolutePath)))
    }
}
