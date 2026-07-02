package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.UseCaseExecutor
import com.jetpackduba.gitnuro.domain.errors.bind
import com.jetpackduba.gitnuro.domain.interfaces.IGetHeadGitAction
import com.jetpackduba.gitnuro.domain.interfaces.IResetToCommitGitAction
import com.jetpackduba.gitnuro.domain.models.Commit
import com.jetpackduba.gitnuro.domain.models.TaskType
import com.jetpackduba.gitnuro.domain.models.UndoableAction
import com.jetpackduba.gitnuro.domain.repositories.RepositoryDataRepository
import javax.inject.Inject

class ResetBranchUseCase @Inject constructor(
    private val resetToCommitGitAction: IResetToCommitGitAction,
    private val getHeadGitAction: IGetHeadGitAction,
    private val repositoryDataRepository: RepositoryDataRepository,
    private val useCaseExecutor: UseCaseExecutor,
    private val refreshAllUseCase: RefreshAllUseCase,
) {
    operator fun invoke(revCommit: Commit, resetType: ResetType) {
        useCaseExecutor.executeLaunch(
            taskType = TaskType.ResetToCommit,
            onRefresh = {
                refreshAllUseCase()
            },
        ) { repositoryPath ->
            // Capture the current branch tip so the reset can be undone
            val head = getHeadGitAction(repositoryPath).bind()
            val previousHash = head.headHash
            if (head.fullBranch.startsWith("refs/heads/") && previousHash != null) {
                val branch = head.fullBranch.removePrefix("refs/heads/")
                repositoryDataRepository.updateLastUndoableAction(
                    UndoableAction.ResetBranch(branch, previousHash, "Reset $branch")
                )
            }

            resetToCommitGitAction(repositoryPath, revCommit, resetType = resetType)
        }
    }
}



enum class ResetType {
    SOFT,
    MIXED,
    HARD,
}