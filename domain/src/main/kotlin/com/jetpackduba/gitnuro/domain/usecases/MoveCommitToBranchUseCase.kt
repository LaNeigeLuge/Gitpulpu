package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.UseCaseExecutor
import com.jetpackduba.gitnuro.domain.interfaces.IMoveCommitToBranchGitAction
import com.jetpackduba.gitnuro.domain.models.TaskType
import javax.inject.Inject

class MoveCommitToBranchUseCase @Inject constructor(
    private val moveCommitToBranchGitAction: IMoveCommitToBranchGitAction,
    private val refreshAllUseCase: RefreshAllUseCase,
    private val useCaseExecutor: UseCaseExecutor,
) {
    operator fun invoke(targetBranch: String) {
        useCaseExecutor.executeLaunch(
            taskType = TaskType.CheckoutBranch,
            onRefresh = { refreshAllUseCase() },
        ) { repositoryPath ->
            moveCommitToBranchGitAction(repositoryPath, targetBranch)
        }
    }
}
