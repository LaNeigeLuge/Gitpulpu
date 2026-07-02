package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.UseCaseExecutor
import com.jetpackduba.gitnuro.domain.interfaces.IDeleteBranchGitAction
import com.jetpackduba.gitnuro.domain.models.Branch
import com.jetpackduba.gitnuro.domain.models.TaskType
import com.jetpackduba.gitnuro.domain.models.UndoableAction
import com.jetpackduba.gitnuro.domain.repositories.RepositoryDataRepository
import javax.inject.Inject

class DeleteBranchUseCase @Inject constructor(
    private val deleteBranchGitAction: IDeleteBranchGitAction,
    private val repositoryDataRepository: RepositoryDataRepository,
    private val useCaseExecutor: UseCaseExecutor,
    private val refreshAllUseCase: RefreshAllUseCase,
) {
    operator fun invoke(branch: Branch) {
        useCaseExecutor.executeLaunch(
            TaskType.DeleteBranch,
            onRefresh = {
                refreshAllUseCase()
            }
        ) { repositoryPath ->
            // Capture the tip so the deleted branch can be recreated in one click
            if (branch.isLocal && branch.hash.isNotBlank()) {
                repositoryDataRepository.updateLastUndoableAction(
                    UndoableAction.DeleteBranch(branch.simpleName, branch.hash, "Delete ${branch.simpleName}")
                )
            }

            deleteBranchGitAction(repositoryPath, branch)
        }
    }
}