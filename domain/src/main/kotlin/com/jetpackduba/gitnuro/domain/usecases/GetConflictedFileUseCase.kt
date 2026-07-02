package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError
import com.jetpackduba.gitnuro.domain.errors.RepositoryPathNotSetError
import com.jetpackduba.gitnuro.domain.interfaces.IGetConflictedFileGitAction
import com.jetpackduba.gitnuro.domain.models.ConflictedFile
import com.jetpackduba.gitnuro.domain.repositories.RepositoryDataRepository
import javax.inject.Inject

class GetConflictedFileUseCase @Inject constructor(
    private val getConflictedFileGitAction: IGetConflictedFileGitAction,
    private val repositoryDataRepository: RepositoryDataRepository,
) {
    suspend operator fun invoke(filePath: String): Either<ConflictedFile, GitError> {
        val repositoryPath = repositoryDataRepository.repositoryPath ?: return Either.Err(RepositoryPathNotSetError)
        return getConflictedFileGitAction(repositoryPath, filePath)
    }
}
