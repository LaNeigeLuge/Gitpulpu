package com.jetpackduba.gitnuro.ui.dialogs

import com.jetpackduba.gitnuro.TabViewModel
import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.usecases.GetWorktreeUseCase
import com.jetpackduba.gitnuro.domain.usecases.RefreshAllUseCase
import com.jetpackduba.gitnuro.domain.usecases.OpenPathInSystemUseCase
import com.jetpackduba.gitnuro.domain.usecases.ResetRepositoryStateUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuickActionsViewModel @Inject constructor(
    private val refreshAllUseCase: RefreshAllUseCase,
    private val getWorktreeUseCase: GetWorktreeUseCase,
    private val openPathInSystemUseCase: OpenPathInSystemUseCase,
    private val resetRepositoryStateUseCase: ResetRepositoryStateUseCase,
) : TabViewModel() {

    // TODO Implement bunch of methods

    fun refreshRepository() = refreshAllUseCase()

    fun resetRepositoryState() = resetRepositoryStateUseCase()

    fun openProjectInFileExplorer() {
        viewModelScope.launch {
            val worktree = getWorktreeUseCase()

            if (worktree is Either.Ok) {
                openPathInSystemUseCase(worktree.value)
            }
        }
    }
}