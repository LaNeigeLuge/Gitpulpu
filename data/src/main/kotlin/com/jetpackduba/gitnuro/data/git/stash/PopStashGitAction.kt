package com.jetpackduba.gitnuro.data.git.stash

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.IPopStashGitAction
import com.jetpackduba.gitnuro.domain.models.Commit
import javax.inject.Inject

class PopStashGitAction @Inject constructor(
    private val applyStashGitAction: ApplyStashGitAction,
    private val deleteStashGitAction: DeleteStashGitAction,
    private val jgit: JGit,
) : IPopStashGitAction {
    override suspend operator fun invoke(repositoryPath: String, stash: Commit) = jgit.provide(repositoryPath) { git ->
        val hasConflicts = applyStashGitAction(git, stash)

        // Like `git stash pop`: keep the stash when the apply conflicted, so it can be retried after a reset.
        if (!hasConflicts) {
            deleteStashGitAction(git, stash)
        }

        hasConflicts
    }
}
