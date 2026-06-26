package com.jetpackduba.gitnuro.ui.dialogs.errors

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpackduba.gitnuro.extensions.handOnHover
import com.jetpackduba.gitnuro.extensions.onDoubleClick
import com.jetpackduba.gitnuro.app.generated.resources.Res
import com.jetpackduba.gitnuro.app.generated.resources.copy
import com.jetpackduba.gitnuro.app.generated.resources.error
import com.jetpackduba.gitnuro.app.generated.resources.error_dialog_copy_button_tooltip
import com.jetpackduba.gitnuro.app.generated.resources.generic_button_ok
import com.jetpackduba.gitnuro.domain.errors.GenericError
import com.jetpackduba.gitnuro.domain.models.TaskType
import com.jetpackduba.gitnuro.domain.repositories.CompletedTask
import com.jetpackduba.gitnuro.theme.secondarySurface
import com.jetpackduba.gitnuro.ui.components.PrimaryButton
import com.jetpackduba.gitnuro.ui.components.tooltip.InstantTooltip
import com.jetpackduba.gitnuro.ui.dialogs.base.MaterialDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Suppress("LongMethod")
@Composable
fun ErrorDialog(
    error: CompletedTask.Failure,
    onAccept: () -> Unit,
) {
    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()
    val clipboard = LocalClipboardManager.current
    val errorStackTrace = remember(error) {
        (error.reason as? GenericError)
            ?.exception
            ?.stackTraceToString()
            .orEmpty()
    }
    val friendlyMessage = remember(error) {
        friendlyErrorMessage(error)
    }
    var showStackTrace by remember { mutableStateOf(false) }

    MaterialDialog(
        onCloseRequested = onAccept,
    ) {
        Column(
            modifier = Modifier
                .width(580.dp)
        ) {
            Row {
                Text(
                    text = error.taskType.errorTitle(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onBackground,
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painterResource(Res.drawable.error),
                    contentDescription = null,
                    tint = MaterialTheme.colors.error,
                    modifier = Modifier.size(24.dp)
                        .onDoubleClick {
                            showStackTrace = !showStackTrace
                        }
                )
            }

            if (friendlyMessage != null) {
                Text(
                    text = friendlyMessage.first,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.body2,
                )
                Text(
                    text = friendlyMessage.second,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium,
                )
            }

            SelectionContainer {
                Text(
                    text = error.reason.toString(),
                    color = if (friendlyMessage != null) MaterialTheme.colors.onBackground.copy(alpha = 0.5f) else MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .padding(top = if (friendlyMessage != null) 12.dp else 16.dp)
                        .widthIn(max = 600.dp),
                    style = MaterialTheme.typography.body2,
                )
            }

            if (showStackTrace && errorStackTrace != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .height(400.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = errorStackTrace.toString(),
                        onValueChange = {},
                        readOnly = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = MaterialTheme.colors.secondarySurface),
                        textStyle = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(horizontalScroll)
                            .verticalScroll(verticalScroll),
                    )

                    HorizontalScrollbar(
                        rememberScrollbarAdapter(horizontalScroll),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    )

                    VerticalScrollbar(
                        rememberScrollbarAdapter(verticalScroll),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                    )

                    InstantTooltip(
                        text = stringResource(Res.string.error_dialog_copy_button_tooltip),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 16.dp)
                    ) {
                        IconButton(
                            onClick = {
                                copyMessageError(clipboard, Exception(error.reason.toString()))
                            },
                            modifier = Modifier
                                .size(24.dp)
                                .handOnHover()
                                .background(MaterialTheme.colors.background.copy(alpha = 0.8f))
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.copy),
                                contentDescription = null,
                                tint = MaterialTheme.colors.onSurface,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 32.dp)
            ) {
                PrimaryButton(
                    text = stringResource(Res.string.generic_button_ok),
                    onClick = onAccept
                )
            }
        }
    }
}

fun copyMessageError(clipboard: ClipboardManager, ex: Exception) {
    clipboard.setText(AnnotatedString(ex.stackTraceToString()))
}

@Suppress("CyclomaticComplexMethod")
fun TaskType.errorTitle(): String {
    return when (this) {
        TaskType.Unspecified -> "Error"
        TaskType.StageAllFiles -> "Staging all the files failed"
        TaskType.UnstageAllFiles -> "Unstaging all the files failed"
        TaskType.StageFile -> "File stage failed"
        TaskType.UnstageFile -> "File unstage failed"
        TaskType.StageHunk -> "File stage failed"
        TaskType.UnstageHunk -> "Hunk unstage failed"
        TaskType.StageLine -> "File line stage failed"
        TaskType.UnstageLine -> "File line unstage failed"
        TaskType.DiscardFile -> "Discard file failed"
        TaskType.DeleteFile -> "Delete file failed"
        TaskType.BlameFile -> "File blaming failed"
        TaskType.HistoryFile -> "Could not load file history"
        TaskType.DoCommit -> "Commit failed"
        TaskType.RevertCommit -> "Commit revert failed"
        TaskType.CherryPickCommit -> "Commit cherry-pick failed"
        TaskType.CheckoutCommit -> "Checkout commit failed"
        TaskType.ResetToCommit -> "Reset to commit failed"
        TaskType.CheckoutBranch -> "Branch checkout failed"
        TaskType.CheckoutRemoteBranch -> "Remote branch checkout failed"
        TaskType.CreateBranch -> "Could not create the new branch"
        TaskType.DeleteBranch -> "Could not delete the branch"
        TaskType.RenameBranch -> "Could not rename the branch"
        TaskType.MergeBranch -> "Merge failed"
        TaskType.RebaseBranch -> "Rebase failed"
        TaskType.RebaseInteractive -> "Rebase interactive failed"
        TaskType.ContinueRebase -> "Could not continue rebase"
        TaskType.AbortRebase -> "Could not abort rebase"
        TaskType.SkipRebase -> "Could not skip rebase step"
        TaskType.ChangeBranchUpstream -> "Upstream branch change failed"
        TaskType.PullFromBranch -> "Pull from branch failed"
        TaskType.PushToBranch -> "Push to branch failed"
        TaskType.DeleteRemoteBranch -> "Deleting remote branch failed"
        TaskType.Pull -> "Pull failed"
        TaskType.Push -> "Push failed"
        TaskType.Fetch -> "Fetch failed"
        TaskType.Stash -> "Stash failed"
        TaskType.ApplyStash -> "Apply stash failed"
        TaskType.PopStash -> "Pop stash failed"
        TaskType.DeleteStash -> "Delete stash failed"
        TaskType.CreateTag -> "Create tag failed"
        TaskType.CheckoutTag -> "Could not checkout tag's commit"
        TaskType.DeleteTag -> "Could not delete tag"
        TaskType.AddSubmodule -> "Add submodule failed"
        TaskType.DeleteSubmodule -> "Delete submodule failed"
        TaskType.InitSubmodule -> "Init submodule failed"
        TaskType.DeinitSubmodule -> "Deinit submodule failed"
        TaskType.SyncSubmodule -> "Sync submodule failed"
        TaskType.UpdateSubmodule -> "Update submodule failed"
        TaskType.SaveCustomTheme -> "Failed trying to save the custom theme"
        TaskType.ResetRepoState -> "Could not reset repository state"
        TaskType.ChangesDetection -> "Repository changes detection has stopped working"
        TaskType.RepositoryOpen -> "Could not open the repository"
        TaskType.RepositoryClone -> "Could not clone the repository"
        TaskType.AddRemote -> "Adding remote failed"
        TaskType.DeleteRemote -> "Deleting remote failed"
        TaskType.LoadAuthor -> "Loading author failed"
        TaskType.StageDir -> "Staging directory failed"
        TaskType.UnstageDir -> "Unstaging directory failed"
        TaskType.SaveAuthor -> "Saving author failed"
        TaskType.GetCommitForRebase -> "Get commit for rebase failed"
        TaskType.GetFileCommits -> "Get file commits failed"
        TaskType.GetLinesForRebaseInteractive -> "Get lines for rebase interactive failed"
        TaskType.PersistCommitMessage -> "Persist commit message failed"
        TaskType.GetCommitDiffEntries -> "Getting commit entries failed"
        TaskType.RefreshBranches -> "Refresh branches failed"
        TaskType.RefreshLog -> "Refresh log failed"
        TaskType.RefreshRemotes -> "Refresh remotes failed"
        TaskType.RefreshRepositoryState -> "Refresh repository state failed"
        TaskType.RefreshStashes -> "Refresh stashes failed"
        TaskType.RefreshStatus -> "Refresh status failed"
        TaskType.RefreshSubmodules -> "Refresh submodules failed"
        TaskType.RefreshTags -> "Refresh tags failed"
        TaskType.GetWorktree -> "Get worktree failed"
        TaskType.UpdateRemote -> "Update remote failed"
    }
}

private fun friendlyErrorMessage(error: CompletedTask.Failure): Pair<String, String>? {
    val message = (error.reason as? GenericError)?.message ?: return null

    return when {
        message.contains("Cannot run program") && message.contains("!") ->
            "Your git credential helper uses a shell command prefix (!) that JGit cannot execute directly." to
                "Fix: Use SSH authentication instead, or configure a credential helper without the ! prefix in your .gitconfig"

        message.contains("not authorized") || message.contains("401") || message.contains("403") ->
            "Authentication failed. Your credentials may be expired or missing." to
                "Fix: Run 'gh auth login' in your terminal or configure an SSH key for this remote."

        message.contains("Could not read from remote") || message.contains("not found") ->
            "Cannot reach the remote repository. It may not exist or you may not have access." to
                "Fix: Check the remote URL with 'git remote -v' and verify you have access."

        message.contains("CONFLICT") || message.contains("conflict") ->
            "There are merge conflicts that need to be resolved manually." to
                "Fix: Open the conflicting files, resolve the markers (<<<< / ====), then stage and commit."

        message.contains("lock") && message.contains(".git") ->
            "Another git process may be running, or a previous operation left a lock file." to
                "Fix: Close other git tools, or delete the .git/*.lock file if no other git process is running."

        message.contains("detached HEAD") ->
            "You are in detached HEAD state — not on any branch." to
                "Fix: Create a new branch from the current state with 'git checkout -b branch-name'."

        else -> null
    }
}