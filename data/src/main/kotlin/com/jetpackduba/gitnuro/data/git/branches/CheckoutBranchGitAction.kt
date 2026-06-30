package com.jetpackduba.gitnuro.data.git.branches

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.ICheckoutBranchGitAction
import com.jetpackduba.gitnuro.domain.models.Branch
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.CheckoutConflictException
import org.eclipse.jgit.api.errors.RefAlreadyExistsException
import javax.inject.Inject

class CheckoutBranchGitAction @Inject constructor(
    private val jgit: JGit,
) : ICheckoutBranchGitAction {
    override suspend operator fun invoke(repositoryPath: String, branch: Branch) = jgit.provide(repositoryPath) { git ->
        try {
            doCheckout(git, branch)
        } catch (_: CheckoutConflictException) {
            autoStashAndCheckout(git, branch)
        }

        Unit
    }

    private fun doCheckout(git: Git, branch: Branch) {
        try {
            git.checkout().apply {
                setName(branch.name)
                if (branch.name.startsWith("refs/remotes/")) {
                    setCreateBranch(true)
                    setName(branch.simpleName)
                    setStartPoint(branch.name)
                    setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                }
                call()
            }
        } catch (_: RefAlreadyExistsException) {
            git.checkout()
                .setName(branch.simpleName)
                .call()
        }
    }

    private fun autoStashAndCheckout(git: Git, branch: Branch) {
        val stashRef = git.stashCreate()
            .setIncludeUntracked(true)
            .call()

        doCheckout(git, branch)

        if (stashRef != null) {
            try {
                git.stashApply()
                    .setStashRef(stashRef.name)
                    .call()
                git.stashDrop()
                    .setStashRef(0)
                    .call()
            } catch (_: Exception) {
                // Stash pop had conflicts — stash is preserved, user can resolve manually
            }
        }
    }
}
