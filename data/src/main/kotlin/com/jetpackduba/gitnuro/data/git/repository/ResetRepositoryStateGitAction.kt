package com.jetpackduba.gitnuro.data.git.repository

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.IResetRepositoryStateGitAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.RebaseCommand
import org.eclipse.jgit.api.ResetCommand
import javax.inject.Inject

class ResetRepositoryStateGitAction @Inject constructor(
    private val jgit: JGit,
) : IResetRepositoryStateGitAction {
    override suspend operator fun invoke(repositoryPath: String) = jgit.provide(repositoryPath) { git ->
        // A rebase must be aborted through the rebase machinery: a plain hard reset
        // does not remove .git/rebase-merge, leaving the repository stuck in
        // REBASING_INTERACTIVE state forever
        if (git.repository.repositoryState.isRebasing) {
            git.rebase()
                .setOperation(RebaseCommand.Operation.ABORT)
                .call()
        }

        git.repository.apply {
            writeMergeCommitMsg(null)
            writeMergeHeads(null)
        }

        git.reset()
            .setMode(ResetCommand.ResetType.HARD)
            .call()

        Unit
    }
}