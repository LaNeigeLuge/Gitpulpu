package com.jetpackduba.gitnuro.data.git.stash

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.exceptions.UncommittedChangesDetectedException
import com.jetpackduba.gitnuro.domain.interfaces.IApplyStashGitAction
import com.jetpackduba.gitnuro.domain.models.Commit
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.StashApplyFailureException
import javax.inject.Inject

class ApplyStashGitAction @Inject constructor(
    private val jgit: JGit,
) : IApplyStashGitAction {
    override suspend operator fun invoke(repositoryPath: String, stashInfo: Commit) = jgit.provide(repositoryPath) { git ->
        invoke(git, stashInfo)
    }

    /**
     * @return true if the stash was applied but left conflicts in the working tree, false if it applied cleanly.
     */
    operator fun invoke(git: Git, stashInfo: Commit): Boolean {
        try {
            git.stashApply()
                .setStashRef(stashInfo.hash)
                .call()
        } catch (ex: StashApplyFailureException) {
            // JGit throws the same exception in two very different cases: the stash was applied and left
            // conflict markers to resolve, or it was rejected without touching anything because local
            // changes would be overwritten. Only the index tells them apart.
            if (git.status().call().conflicting.isEmpty()) {
                throw UncommittedChangesDetectedException(
                    "Your local changes to these files would be overwritten by the stash. " +
                        "Commit them or stash them first, then retry.",
                )
            }

            return true
        }

        return false
    }
}
