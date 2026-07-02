package com.jetpackduba.gitnuro.data.git.conflicts

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.IResolveConflictGitAction
import java.io.File
import javax.inject.Inject

class ResolveConflictGitAction @Inject constructor(
    private val jgit: JGit,
) : IResolveConflictGitAction {
    override suspend operator fun invoke(
        repositoryPath: String,
        filePath: String,
        resolvedContent: String,
    ) = jgit.provide(repositoryPath) { git ->
        val file = File(git.repository.workTree, filePath)
        file.writeText(resolvedContent)

        // Staging a previously-conflicting path clears its conflict entry in the index
        git.add()
            .addFilepattern(filePath)
            .call()

        Unit
    }
}
