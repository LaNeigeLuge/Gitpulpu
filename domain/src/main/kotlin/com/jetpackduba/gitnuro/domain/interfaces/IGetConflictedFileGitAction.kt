package com.jetpackduba.gitnuro.domain.interfaces

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError
import com.jetpackduba.gitnuro.domain.models.ConflictedFile

interface IGetConflictedFileGitAction {
    /** Reads the working-tree content of [filePath] and parses its conflict markers. */
    suspend operator fun invoke(repositoryPath: String, filePath: String): Either<ConflictedFile, GitError>
}
