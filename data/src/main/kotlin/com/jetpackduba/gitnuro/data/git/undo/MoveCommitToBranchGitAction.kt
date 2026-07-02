package com.jetpackduba.gitnuro.data.git.undo

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.IMoveCommitToBranchGitAction
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.lib.ObjectId
import javax.inject.Inject

class MoveCommitToBranchGitAction @Inject constructor(
    private val jgit: JGit,
) : IMoveCommitToBranchGitAction {
    override suspend operator fun invoke(repositoryPath: String, targetBranch: String) =
        jgit.provide(repositoryPath) { git ->
            // The commit currently sitting on the detached HEAD
            val commit = git.repository.resolve("HEAD")
                ?: throw Exception("Could not resolve HEAD")

            // Attach to the target branch first
            git.checkout().setName(targetBranch).call()

            // Try a fast-forward: if the branch is an ancestor of the commit, its tip just
            // moves up to the commit — lossless, keeps the same hash ("move").
            val ffResult = git.merge()
                .include(commit)
                .setFastForward(MergeCommand.FastForwardMode.FF_ONLY)
                .call()

            val moved = ffResult.mergeStatus == MergeResult.MergeStatus.FAST_FORWARD ||
                ffResult.mergeStatus == MergeResult.MergeStatus.ALREADY_UP_TO_DATE

            if (!moved) {
                // Branch has diverged — bring the commit's changes over as a new commit instead
                git.cherryPick().include(commit).call()
            }

            Unit
        }
}
