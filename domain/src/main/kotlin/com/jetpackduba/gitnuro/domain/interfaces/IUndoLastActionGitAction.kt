package com.jetpackduba.gitnuro.domain.interfaces

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError
import com.jetpackduba.gitnuro.domain.models.UndoableAction

interface IUndoLastActionGitAction {
    suspend operator fun invoke(repositoryPath: String, action: UndoableAction): Either<Unit, GitError>
}
