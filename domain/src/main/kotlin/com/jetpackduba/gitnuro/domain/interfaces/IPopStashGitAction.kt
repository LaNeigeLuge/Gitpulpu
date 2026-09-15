package com.jetpackduba.gitnuro.domain.interfaces

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError
import com.jetpackduba.gitnuro.domain.models.Commit
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

interface IPopStashGitAction {
    /** @return true if the stash was applied but left conflicts to resolve (the stash is kept in that case). */
    suspend operator fun invoke(repositoryPath: String, stash: Commit): Either<Boolean, GitError>
}