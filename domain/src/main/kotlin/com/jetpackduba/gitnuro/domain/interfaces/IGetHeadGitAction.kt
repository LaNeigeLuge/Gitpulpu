package com.jetpackduba.gitnuro.domain.interfaces

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError

/** Current HEAD position: a full ref name (refs/heads/x) or a commit hash when detached. */
data class HeadInfo(val fullBranch: String, val headHash: String?)

interface IGetHeadGitAction {
    suspend operator fun invoke(repositoryPath: String): Either<HeadInfo, GitError>
}
