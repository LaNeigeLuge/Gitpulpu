package com.jetpackduba.gitnuro.domain.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

private const val DEFAULT_MAX_DEPTH = 5

// Dependency/output dirs that never contain repos worth listing. Hidden dirs (starting with '.')
// are skipped separately, which already excludes .cargo, .pyenv, .gradle, .m2, etc.
private val DEFAULT_IGNORED_DIRS = setOf(
    "node_modules", "target", "build", "vendor", "Pods", "DerivedData", "__pycache__",
)

/**
 * Discovers git repositories on the machine by walking a set of root directories.
 *
 * Cross-platform by design: the default root is the user's home (works on every OS/locale) and the
 * walk makes no assumptions about folder names. It is kept fast and noise-free by bounding the depth,
 * skipping hidden and dependency directories, and not following symlinks (avoids cycles and network
 * mounts). A directory is treated as a repo if it contains a `.git` entry — a directory for normal
 * clones, a file for worktrees/submodules — and the walk does not descend into a repo once found.
 */
class ScanRepositoriesUseCase @Inject constructor() {
    suspend operator fun invoke(
        roots: List<String> = listOf(System.getProperty("user.home")),
        maxDepth: Int = DEFAULT_MAX_DEPTH,
        ignoredDirs: Set<String> = DEFAULT_IGNORED_DIRS,
    ): List<String> = withContext(Dispatchers.IO) {
        val found = LinkedHashSet<String>()

        for (root in roots) {
            val rootFile = File(root)
            if (rootFile.isDirectory) {
                scan(rootFile, maxDepth, ignoredDirs, found)
            }
        }

        found.sorted()
    }

    private fun scan(dir: File, depthRemaining: Int, ignoredDirs: Set<String>, out: MutableSet<String>) {
        if (File(dir, ".git").exists()) {
            out.add(dir.absolutePath)
            return // a repo — record it and stop; don't list its submodules as separate repos
        }

        if (depthRemaining <= 0) return

        val children = dir.listFiles() ?: return // null on permission errors — skip quietly
        for (child in children) {
            if (!child.isDirectory) continue
            if (child.name.startsWith(".")) continue
            if (child.name in ignoredDirs) continue
            if (isSymlink(child)) continue

            scan(child, depthRemaining - 1, ignoredDirs, out)
        }
    }

    private fun isSymlink(file: File): Boolean = try {
        file.canonicalFile != file.absoluteFile
    } catch (e: Exception) {
        true // if we can't resolve it, treat as a symlink and skip rather than risk a loop
    }
}
