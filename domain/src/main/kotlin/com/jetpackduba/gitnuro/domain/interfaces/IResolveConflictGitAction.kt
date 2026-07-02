package com.jetpackduba.gitnuro.domain.interfaces

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError

interface IResolveConflictGitAction {
    /** Writes [resolvedContent] to [filePath] and stages it, marking the conflict resolved. */
    suspend operator fun invoke(
        repositoryPath: String,
        filePath: String,
        resolvedContent: String,
    ): Either<Unit, GitError>
}
