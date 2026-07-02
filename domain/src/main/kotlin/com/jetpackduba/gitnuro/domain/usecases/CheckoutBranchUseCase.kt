package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.UseCaseExecutor
import com.jetpackduba.gitnuro.domain.errors.bind
import com.jetpackduba.gitnuro.domain.interfaces.ICheckoutBranchGitAction
import com.jetpackduba.gitnuro.domain.interfaces.IGetHeadGitAction
import com.jetpackduba.gitnuro.domain.models.Branch
import com.jetpackduba.gitnuro.domain.models.TaskType
import com.jetpackduba.gitnuro.domain.models.UndoableAction
import com.jetpackduba.gitnuro.domain.repositories.RepositoryDataRepository
import javax.inject.Inject

class CheckoutBranchUseCase @Inject constructor(
    private val checkoutBranchGitAction: ICheckoutBranchGitAction,
    private val getHeadGitAction: IGetHeadGitAction,
    private val repositoryDataRepository: RepositoryDataRepository,
    private val useCaseExecutor: UseCaseExecutor,
    private val refreshAllUseCase: RefreshAllUseCase,
) {
    operator fun invoke(branch: Branch) {
        useCaseExecutor.executeLaunch(
            taskType = TaskType.CheckoutBranch,
            onRefresh = {
                refreshAllUseCase()
            }
        ) { repositoryPath ->
            // Capture where we are so the checkout can be undone
            val head = getHeadGitAction(repositoryPath).bind()
            repositoryDataRepository.updateLastUndoableAction(
                UndoableAction.Checkout(head.fullBranch, "Checkout ${branch.simpleName}")
            )

            checkoutBranchGitAction(repositoryPath, branch)
        }
    }
}