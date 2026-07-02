package com.jetpackduba.gitnuro.data.git.undo

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.HeadInfo
import com.jetpackduba.gitnuro.domain.interfaces.IGetHeadGitAction
import javax.inject.Inject

class GetHeadGitAction @Inject constructor(
    private val jgit: JGit,
) : IGetHeadGitAction {
    override suspend operator fun invoke(repositoryPath: String) = jgit.provide(repositoryPath) { git ->
        HeadInfo(
            fullBranch = git.repository.fullBranch,
            headHash = git.repository.resolve("HEAD")?.name,
        )
    }
}
