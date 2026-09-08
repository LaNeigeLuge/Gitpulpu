package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.UseCaseExecutor
import com.jetpackduba.gitnuro.domain.interfaces.IPushTagGitAction
import com.jetpackduba.gitnuro.domain.models.Tag
import com.jetpackduba.gitnuro.domain.models.TaskType
import javax.inject.Inject

class PushTagUseCase @Inject constructor(
    private val useCaseExecutor: UseCaseExecutor,
    private val pushTagGitAction: IPushTagGitAction,
    private val refreshAllUseCase: RefreshAllUseCase,
) {
    operator fun invoke(tag: Tag) = useCaseExecutor.executeLaunch(
        taskType = TaskType.PushTag,
        onRefresh = {
            refreshAllUseCase()
        }
    ) { repositoryPath ->
        pushTagGitAction(repositoryPath, tag)
    }
}
