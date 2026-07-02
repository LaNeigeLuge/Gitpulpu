package com.jetpackduba.gitnuro.data.git.conflicts

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.ConflictParser
import com.jetpackduba.gitnuro.domain.interfaces.IGetConflictedFileGitAction
import java.io.File
import javax.inject.Inject

class GetConflictedFileGitAction @Inject constructor(
    private val jgit: JGit,
) : IGetConflictedFileGitAction {
    override suspend operator fun invoke(repositoryPath: String, filePath: String) =
        jgit.provide(repositoryPath) { git ->
            val workTree = git.repository.workTree
            val file = File(workTree, filePath)
            val content = if (file.exists()) file.readText() else ""

            ConflictParser.parse(filePath, content)
        }
}
