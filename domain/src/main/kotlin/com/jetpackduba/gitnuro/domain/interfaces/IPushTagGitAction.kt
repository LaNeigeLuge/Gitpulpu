package com.jetpackduba.gitnuro.domain.interfaces

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError
import com.jetpackduba.gitnuro.domain.models.Tag

interface IPushTagGitAction {
    suspend operator fun invoke(repositoryPath: String, tag: Tag): Either<Unit, GitError>
}
