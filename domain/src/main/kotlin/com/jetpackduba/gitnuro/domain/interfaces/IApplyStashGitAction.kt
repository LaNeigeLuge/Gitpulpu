package com.jetpackduba.gitnuro.domain.interfaces

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError
import com.jetpackduba.gitnuro.domain.models.Commit

interface IApplyStashGitAction {
    /** @return true if the stash was applied but left conflicts to resolve. */
    suspend operator fun invoke(repositoryPath: String, stashInfo: Commit): Either<Boolean, GitError>
}