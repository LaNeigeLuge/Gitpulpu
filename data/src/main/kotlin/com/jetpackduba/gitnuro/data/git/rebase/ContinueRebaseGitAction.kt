package com.jetpackduba.gitnuro.data.git.rebase

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.IContinueRebaseGitAction
import org.eclipse.jgit.api.RebaseCommand
import org.eclipse.jgit.lib.RebaseTodoLine
import java.io.File
import javax.inject.Inject

class ContinueRebaseGitAction @Inject constructor(
    private val jgit: JGit,
) : IContinueRebaseGitAction {
    override suspend operator fun invoke(repositoryPath: String) = jgit.provide(repositoryPath) { git ->
        val doneFile = File(git.repository.directory, "${RebaseConstants.REBASE_MERGE}/${RebaseConstants.DONE}")

        // JGit's CONTINUE reads rebase-merge/done, which only exists once a step has been applied.
        // An interactive rebase still awaiting its todo plan has no "done" yet, so continuing there
        // must run the plan (PROCESS_STEPS) instead of resuming a step that never started.
        if (doneFile.exists()) {
            git.rebase()
                .setOperation(RebaseCommand.Operation.CONTINUE)
                .call()
        } else {
            git.rebase()
                .runInteractively(KeepStepsAsIsHandler)
                .setOperation(RebaseCommand.Operation.PROCESS_STEPS)
                .call()
        }

        // TODO Throw error if call result is not continue?
        Unit
    }
}

/** Applies the todo plan already on disk, leaving commit messages untouched. */
private object KeepStepsAsIsHandler : RebaseCommand.InteractiveHandler {
    override fun prepareSteps(steps: MutableList<RebaseTodoLine>?) = Unit
    override fun modifyCommitMessage(message: String?): String = message.orEmpty()
}
