package com.jetpackduba.gitnuro.data.git.remote_operations

import com.jetpackduba.gitnuro.data.git.JGit
import com.jetpackduba.gitnuro.data.git.branches.GetTrackingBranchGitAction
import com.jetpackduba.gitnuro.domain.errors.bind
import com.jetpackduba.gitnuro.domain.interfaces.IHandleTransportGitAction
import com.jetpackduba.gitnuro.domain.interfaces.IPushTagGitAction
import com.jetpackduba.gitnuro.domain.models.Tag
import com.jetpackduba.gitnuro.domain.models.isRejected
import com.jetpackduba.gitnuro.domain.models.statusMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.transport.RefSpec
import javax.inject.Inject

/**
 * Pushes a single tag by its own refspec. Deliberately not `--tags`: that would publish every
 * local tag, including any pulled in from another remote.
 */
class PushTagGitAction @Inject constructor(
    private val handleTransportGitAction: IHandleTransportGitAction,
    private val getTrackingBranchGitAction: GetTrackingBranchGitAction,
    private val jgit: JGit,
) : IPushTagGitAction {
    override suspend operator fun invoke(repositoryPath: String, tag: Tag) = jgit.provide(repositoryPath) { git ->
        val tracking = getTrackingBranchGitAction(repositoryPath, git.repository.branch).bind()

        handleTransportGitAction(repositoryPath) {
            withContext(Dispatchers.IO) {
                val pushResult = git
                    .push()
                    .setRefSpecs(RefSpec("${tag.name}:${tag.name}"))
                    .run { if (tracking != null) setRemote(tracking.remote) else this }
                    .setTransportConfigCallback { handleTransport(it) }
                    .call()

                val rejected = pushResult
                    .map { result -> result.remoteUpdates.filter { it.status.isRejected } }
                    .flatten()

                if (rejected.isNotEmpty()) {
                    throw Exception(rejected.joinToString("\n") { it.statusMessage ?: it.status.name })
                }
            }
        }.bind()

        Unit
    }
}
