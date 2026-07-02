package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.ConflictParser
import com.jetpackduba.gitnuro.domain.UseCaseExecutor
import com.jetpackduba.gitnuro.domain.errors.bind
import com.jetpackduba.gitnuro.domain.interfaces.IGetConflictedFileGitAction
import com.jetpackduba.gitnuro.domain.interfaces.IResolveConflictGitAction
import com.jetpackduba.gitnuro.domain.models.ConflictChoice
import com.jetpackduba.gitnuro.domain.models.TaskType
import javax.inject.Inject

class ResolveConflictUseCase @Inject constructor(
    private val useCaseExecutor: UseCaseExecutor,
    private val getConflictedFileGitAction: IGetConflictedFileGitAction,
    private val resolveConflictGitAction: IResolveConflictGitAction,
    private val refreshStatusUseCase: RefreshStatusUseCase,
) {
    /** Writes fully-formed resolved content (from the merge editor) and stages it. */
    fun withContent(filePath: String, resolvedContent: String) {
        useCaseExecutor.executeLaunch(
            taskType = TaskType.StageFile,
            onRefresh = { refreshStatusUseCase() },
        ) { repositoryPath ->
            resolveConflictGitAction(repositoryPath, filePath, resolvedContent)
        }
    }

    /** Convenience for whole-file "use ours" / "use theirs": re-parse and pick one side for every conflict. */
    fun withChoice(filePath: String, choice: ConflictChoice) {
        useCaseExecutor.executeLaunch(
            taskType = TaskType.StageFile,
            onRefresh = { refreshStatusUseCase() },
        ) { repositoryPath ->
            val file = getConflictedFileGitAction(repositoryPath, filePath).bind()
            val choices = file.blocks
                .filterIsInstance<com.jetpackduba.gitnuro.domain.models.ConflictBlock.Conflict>()
                .indices
                .associateWith { choice }
            val resolved = ConflictParser.resolve(file, choices)

            resolveConflictGitAction(repositoryPath, filePath, resolved)
        }
    }
}
