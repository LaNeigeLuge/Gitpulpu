package com.jetpackduba.gitnuro.data.git.rebase

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.domain.interfaces.IGetRebaseSourceGitAction
import com.jetpackduba.gitnuro.domain.models.RebaseSource
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import java.io.File
import javax.inject.Inject

class GetRebaseSourceGitAction @Inject constructor(
    private val jgit: JGit,
) : IGetRebaseSourceGitAction {
    override suspend operator fun invoke(repositoryPath: String) = jgit.provide(repositoryPath) { git ->
        val rebaseMergeDir = File(git.repository.directory, RebaseConstants.REBASE_MERGE)
        val ontoHash = File(rebaseMergeDir, RebaseConstants.ONTO).takeIf { it.isFile }?.readText()?.trim()

        if (ontoHash.isNullOrEmpty()) {
            return@provide null
        }

        val headName = File(rebaseMergeDir, RebaseConstants.HEAD_NAME).takeIf { it.isFile }?.readText()?.trim()

        // "onto" is stored as a raw hash; name it after a branch when one points at it, local first
        val refDatabase = git.repository.refDatabase
        val ontoRef = (refDatabase.getRefsByPrefix(Constants.R_HEADS) + refDatabase.getRefsByPrefix(Constants.R_REMOTES))
            .firstOrNull { it.objectId?.name == ontoHash }

        RebaseSource(
            branchName = headName?.takeIf { it != Constants.HEAD }?.let { Repository.shortenRefName(it) },
            ontoName = ontoRef?.let { Repository.shortenRefName(it.name) } ?: ontoHash.take(7),
            ontoHash = ontoHash,
        )
    }
}
