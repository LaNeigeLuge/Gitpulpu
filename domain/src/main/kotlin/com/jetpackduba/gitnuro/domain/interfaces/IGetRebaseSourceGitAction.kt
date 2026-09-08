package com.jetpackduba.gitnuro.domain.interfaces

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError
import com.jetpackduba.gitnuro.domain.models.RebaseSource

interface IGetRebaseSourceGitAction {
    /** Null when no rebase is in progress. */
    suspend operator fun invoke(repositoryPath: String): Either<RebaseSource?, GitError>
}
