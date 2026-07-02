package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.UseCaseExecutor
import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.interfaces.IUndoLastActionGitAction
import com.jetpackduba.gitnuro.domain.models.TaskType
import com.jetpackduba.gitnuro.domain.repositories.RepositoryDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UndoLastActionUseCase @Inject constructor(
    private val undoLastActionGitAction: IUndoLastActionGitAction,
    private val repositoryDataRepository: RepositoryDataRepository,
    private val refreshAllUseCase: RefreshAllUseCase,
    private val useCaseExecutor: UseCaseExecutor,
) {
    operator fun invoke() {
        useCaseExecutor.executeLaunch(
            taskType = TaskType.Unspecified,
            onRefresh = { refreshAllUseCase() },
        ) { repositoryPath ->
            val action = repositoryDataRepository.lastUndoableAction.first()
                ?: return@executeLaunch Either.Ok(Unit)

            val result = undoLastActionGitAction(repositoryPath, action)
            // Consume it either way — a failed undo shouldn't leave a stale, misleading offer
            repositoryDataRepository.updateLastUndoableAction(null)
            result
        }
    }
}
