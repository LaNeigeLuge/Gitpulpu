package com.jetpackduba.gitnuro.domain.usecases

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError
import com.jetpackduba.gitnuro.domain.errors.RepositoryPathNotSetError
import com.jetpackduba.gitnuro.domain.errors.bind
import com.jetpackduba.gitnuro.domain.errors.either
import com.jetpackduba.gitnuro.domain.interfaces.IGetCommitFromRebaseLineGitAction
import com.jetpackduba.gitnuro.domain.interfaces.IGetRebaseInteractiveTodoLinesGitAction
import com.jetpackduba.gitnuro.domain.models.RebaseLine
import com.jetpackduba.gitnuro.domain.repositories.RepositoryDataRepository
import javax.inject.Inject

/** Todo lines for the interactive rebase plus each commit's full message (keyed by commit hash). */
data class RebaseInteractiveData(
    val lines: List<RebaseLine>,
    val messages: Map<String, String>,
)

class LoadRebaseInteractiveUseCase @Inject constructor(
    private val getRebaseInteractiveTodoLinesGitAction: IGetRebaseInteractiveTodoLinesGitAction,
    private val getCommitFromRebaseLineGitAction: IGetCommitFromRebaseLineGitAction,
    private val repositoryDataRepository: RepositoryDataRepository,
) {
    suspend operator fun invoke(): Either<RebaseInteractiveData, GitError> {
        val path = repositoryDataRepository.repositoryPath ?: return Either.Err(RepositoryPathNotSetError)

        return either {
            val lines = getRebaseInteractiveTodoLinesGitAction(path).bind()

            val messages = lines.associate { line ->
                val commit = getCommitFromRebaseLineGitAction(path, line.commit, line.shortMessage).bind()
                line.commit to (commit?.message ?: line.shortMessage)
            }

            Either.Ok(RebaseInteractiveData(lines, messages))
        }
    }
}
