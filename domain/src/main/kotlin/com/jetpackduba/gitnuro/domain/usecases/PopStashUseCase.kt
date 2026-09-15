package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.UseCaseExecutor
import com.jetpackduba.gitnuro.domain.errors.GenericError
import com.jetpackduba.gitnuro.domain.errors.bind
import com.jetpackduba.gitnuro.domain.errors.raiseError
import com.jetpackduba.gitnuro.domain.interfaces.IGetStashListGitAction
import com.jetpackduba.gitnuro.domain.interfaces.IPopStashGitAction
import com.jetpackduba.gitnuro.domain.models.Commit
import com.jetpackduba.gitnuro.domain.models.TaskType
import javax.inject.Inject

class PopStashUseCase @Inject constructor(
    private val popStashGitAction: IPopStashGitAction,
    private val getStashListGitAction: IGetStashListGitAction,
    private val useCaseExecutor: UseCaseExecutor,
    private val refreshStatusUseCase: RefreshStatusUseCase,
    private val refreshLogUseCase: RefreshLogUseCase,
    private val refreshStashListUseCase: RefreshStashListUseCase,
) {
    operator fun invoke(commit: Commit?) = useCaseExecutor.executeLaunch(
        taskType = TaskType.Stash,
        refreshEvenIfFailed = true,
        onRefresh = {
            refreshStatusUseCase()
            refreshLogUseCase()
            refreshStashListUseCase()
        }
    ) { repositoryPath ->
        val stashes = getStashListGitAction(repositoryPath).bind()
        val stashCommit = commit ?: stashes.firstOrNull()

        if (stashCommit == null) {
            raiseError(GenericError("No stashes found")) // TODO Refactor this to a proper type
        }

        // The selection may point at a stash that has already been popped or dropped; applying it again
        // would silently re-apply a dangling commit.
        if (stashes.none { it.hash == stashCommit.hash }) {
            raiseError(GenericError("This stash no longer exists"))
        }

        popStashGitAction(repositoryPath, stashCommit)
    }
}