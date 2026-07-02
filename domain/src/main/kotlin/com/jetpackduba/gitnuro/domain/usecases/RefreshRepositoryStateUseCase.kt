package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.UseCaseExecutor
import com.jetpackduba.gitnuro.domain.errors.bind
import com.jetpackduba.gitnuro.domain.interfaces.IGetHeadGitAction
import com.jetpackduba.gitnuro.domain.interfaces.IGetRebaseInteractiveStateGitAction
import com.jetpackduba.gitnuro.domain.interfaces.IGetRepositoryStateGitAction
import com.jetpackduba.gitnuro.domain.repositories.RepositoryDataRepository
import javax.inject.Inject

/**
 * Reads git's on-disk repository state (SAFE, MERGING, REBASING_INTERACTIVE, …) and the
 * interactive rebase state, and pushes them into the data repository flows. Without this the
 * UI never learns that an operation is in progress, so the rebase view, the status-in-progress
 * banner, and the Continue/Abort buttons never appear.
 */
class RefreshRepositoryStateUseCase @Inject constructor(
    private val getRepositoryStateGitAction: IGetRepositoryStateGitAction,
    private val getRebaseInteractiveStateGitAction: IGetRebaseInteractiveStateGitAction,
    private val getHeadGitAction: IGetHeadGitAction,
    private val repositoryDataRepository: RepositoryDataRepository,
    private val useCaseExecutor: UseCaseExecutor,
) {
    operator fun invoke() {
        useCaseExecutor.executeOnTabScope { repositoryPath ->
            val state = getRepositoryStateGitAction(repositoryPath).bind()
            repositoryDataRepository.updateRepositoryState(state)

            val rebaseState = getRebaseInteractiveStateGitAction(repositoryPath).bind()
            repositoryDataRepository.updateRebaseInteractiveState(rebaseState)

            // Detached HEAD: fullBranch is a commit hash rather than a refs/heads/ ref
            val head = getHeadGitAction(repositoryPath).bind()
            repositoryDataRepository.updateIsHeadDetached(!head.fullBranch.startsWith("refs/heads/"))
        }
    }
}
