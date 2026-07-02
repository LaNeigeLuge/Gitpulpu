package com.jetpackduba.gitnuro.data.git.undo

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.IUndoLastActionGitAction
import com.jetpackduba.gitnuro.domain.models.UndoableAction
import org.eclipse.jgit.api.ResetCommand
import javax.inject.Inject

class UndoLastActionGitAction @Inject constructor(
    private val jgit: JGit,
) : IUndoLastActionGitAction {
    override suspend operator fun invoke(repositoryPath: String, action: UndoableAction) =
        jgit.provide(repositoryPath) { git ->
            when (action) {
                is UndoableAction.Checkout -> {
                    git.checkout().setName(action.previousRef).call()
                }

                is UndoableAction.ResetBranch -> {
                    // Move the branch tip back to where it was. Requires being on that branch.
                    git.checkout().setName(action.branch).call()
                    git.reset()
                        .setMode(ResetCommand.ResetType.HARD)
                        .setRef(action.previousHash)
                        .call()
                }

                is UndoableAction.DeleteBranch -> {
                    git.branchCreate()
                        .setName(action.branch)
                        .setStartPoint(action.hash)
                        .call()
                }
            }

            Unit
        }
}
