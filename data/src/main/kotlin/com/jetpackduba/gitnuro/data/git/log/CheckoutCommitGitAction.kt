package com.jetpackduba.gitnuro.data.git.log

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.ICheckoutCommitGitAction
import com.jetpackduba.gitnuro.domain.models.Commit
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.CheckoutConflictException
import javax.inject.Inject

class CheckoutCommitGitAction @Inject constructor(
    private val jgit: JGit,
) : ICheckoutCommitGitAction {
    override suspend operator fun invoke(repositoryPath: String, commit: Commit) =
        this@CheckoutCommitGitAction(repositoryPath, commit.hash)

    override suspend operator fun invoke(repositoryPath: String, hash: String) = jgit.provide(repositoryPath) { git ->
        try {
            git.checkout()
                .setName(hash)
                .call()
        } catch (_: CheckoutConflictException) {
            val stashRef = git.stashCreate()
                .setIncludeUntracked(true)
                .call()

            git.checkout()
                .setName(hash)
                .call()

            if (stashRef != null) {
                try {
                    git.stashApply()
                        .setStashRef(stashRef.name)
                        .call()
                    git.stashDrop()
                        .setStashRef(0)
                        .call()
                } catch (_: Exception) {
                    // Stash pop had conflicts — stash is preserved
                }
            }
        }

        Unit
    }
}
