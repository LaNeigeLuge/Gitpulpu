package com.jetpackduba.gitnuro.domain.interfaces

import com.jetpackduba.gitnuro.domain.errors.Either
import com.jetpackduba.gitnuro.domain.errors.GitError

interface IMoveCommitToBranchGitAction {
    /**
     * Moves the current (detached HEAD) commit onto [targetBranch] and checks that branch out.
     * Fast-forwards the branch when the commit builds directly on it (lossless, same hash);
     * otherwise cherry-picks the commit onto the branch.
     */
    suspend operator fun invoke(repositoryPath: String, targetBranch: String): Either<Unit, GitError>
}
