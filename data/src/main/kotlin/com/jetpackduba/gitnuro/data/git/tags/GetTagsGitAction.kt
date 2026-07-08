package com.jetpackduba.gitnuro.data.git.tags

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.IGetTagsGitAction
import com.jetpackduba.gitnuro.domain.models.Tag
import javax.inject.Inject

class GetTagsGitAction @Inject constructor(
    private val jgit: JGit,
) : IGetTagsGitAction {
    override suspend operator fun invoke(repositoryPath: String) = jgit.provide(repositoryPath) { git ->
        val refDatabase = git.repository.refDatabase

        git
            .tagList()
            .call()
            .map { ref ->
                // Peel the ref so annotated tags resolve to their underlying commit.
                // peeledObjectId is set only for annotated tags; for lightweight tags the
                // ref's own objectId already points directly at the commit.
                val peeled = refDatabase.peel(ref)
                val commitId = peeled.peeledObjectId ?: ref.objectId

                Tag(
                    commitHash = commitId.name,
                    hash = ref.objectId.name,
                    name = ref.name,
                )
            }
    }
}
