package com.jetpackduba.gitnuro.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpackduba.gitnuro.extensions.backgroundIf
import com.jetpackduba.gitnuro.app.generated.resources.*
import com.jetpackduba.gitnuro.domain.models.ui.SelectedItem
import com.jetpackduba.gitnuro.theme.AppShapes
import com.jetpackduba.gitnuro.theme.backgroundSelected
import com.jetpackduba.gitnuro.theme.onBackgroundSecondary
import com.jetpackduba.gitnuro.ui.components.AdjustableOutlinedTextField
import com.jetpackduba.gitnuro.ui.components.PrimaryButton
import com.jetpackduba.gitnuro.ui.components.ScrollableLazyColumn
import com.jetpackduba.gitnuro.ui.drag_sorting.VerticalDraggableItem
import com.jetpackduba.gitnuro.ui.drag_sorting.rememberVerticalDragDropState
import com.jetpackduba.gitnuro.ui.drag_sorting.verticalDragContainer
import com.jetpackduba.gitnuro.viewmodels.RebaseAction
import com.jetpackduba.gitnuro.viewmodels.RebaseInteractiveViewState
import com.jetpackduba.gitnuro.viewmodels.RebaseLine
import com.jetpackduba.gitnuro.repositoryopen.RepositoryOpenViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RebaseInteractive(
    viewModel: RepositoryOpenViewModel,
) {
    val rebaseState = viewModel.rebaseState.collectAsState()
    val rebaseStateValue = rebaseState.value
    val selectedItem = viewModel.selectedItem.collectAsState().value

    LaunchedEffect(viewModel) {
        viewModel.loadRebaseInteractiveData()
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .fillMaxSize(),
    ) {
        when (rebaseStateValue) {
            is RebaseInteractiveViewState.Failed -> {}
            is RebaseInteractiveViewState.Loaded -> {
                RebaseStateLoaded(
                    viewModel,
                    rebaseStateValue,
                    selectedItem,
                    onFocusLine = {
                        if (
                            selectedItem !is SelectedItem.CommitItem ||
                            !selectedItem.commit.hash.startsWith(it.commit.name())
                        ) {
                            viewModel.selectLine(it)
                        }
                    },
                    onCancel = {
                        viewModel.cancel()
                    },
                    onMoveCommit = { from, to ->
                        viewModel.moveCommit(from, to)
                    }
                )
            }

            RebaseInteractiveViewState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RebaseStateLoaded(
    viewModel: RepositoryOpenViewModel,
    rebaseState: RebaseInteractiveViewState.Loaded,
    selectedItem: SelectedItem,
    onFocusLine: (RebaseLine) -> Unit,
    onCancel: () -> Unit,
    onMoveCommit: (from: Int, to: Int) -> Unit,
) {
    val stepsList = rebaseState.stepsList

    Column(
        modifier = Modifier.fillMaxSize()
            .clip(AppShapes.small)
            .background(MaterialTheme.colors.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.rebase_interactive_view_title),
                color = MaterialTheme.colors.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "${stepsList.size} commits",
                color = MaterialTheme.colors.onBackgroundSecondary,
                fontSize = 12.sp,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Legend
            RebaseActionLegend()
        }

        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.08f))

        val listState = rememberLazyListState()
        val state = rememberVerticalDragDropState(listState) { fromIndex, toIndex ->
            onMoveCommit(fromIndex, toIndex)
        }

        ScrollableLazyColumn(
            modifier = Modifier
                .weight(1f)
                .verticalDragContainer(state, onDraggedItem = {}),
            state = listState,
        ) {
            itemsIndexed(
                stepsList,
                key = { _, line -> line.commit },
            ) { index, rebaseTodoLine ->
                VerticalDraggableItem(state, index) {
                    RebaseCommit(
                        rebaseLine = rebaseTodoLine,
                        message = rebaseState.messages[rebaseTodoLine.commit.name()],
                        isSelected = selectedItem is SelectedItem.CommitItem && selectedItem.commit.hash.startsWith(
                            rebaseTodoLine.commit.name()
                        ),
                        isFirst = stepsList.first() == rebaseTodoLine,
                        onFocusLine = { onFocusLine(rebaseTodoLine) },
                        onActionChanged = { newAction ->
                            viewModel.onCommitActionChanged(rebaseTodoLine.commit, newAction)
                        },
                        onMessageChanged = { newMessage ->
                            viewModel.onCommitMessageChanged(rebaseTodoLine.commit, newMessage)
                        },
                    )
                }
            }
        }

        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.08f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(Res.string.generic_button_cancel),
                modifier = Modifier.padding(end = 8.dp),
                onClick = onCancel,
                backgroundColor = Color.Transparent,
                textColor = MaterialTheme.colors.onBackground,
            )

            PrimaryButton(
                modifier = Modifier,
                enabled = true,
                onClick = {
                    viewModel.continueRebaseInteractive()
                },
                text = stringResource(Res.string.rebase_interactive_view_button_complete_rebase)
            )
        }
    }
}

@Composable
private fun RebaseActionLegend() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (action in listOf(RebaseAction.PICK, RebaseAction.REWORD, RebaseAction.SQUASH, RebaseAction.DROP)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(AppShapes.small)
                        .background(action.color())
                )
                Text(
                    text = action.displayName,
                    color = MaterialTheme.colors.onBackgroundSecondary,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@Composable
fun RebaseCommit(
    rebaseLine: RebaseLine,
    isFirst: Boolean,
    isSelected: Boolean,
    message: String?,
    onFocusLine: () -> Unit,
    onActionChanged: (RebaseAction) -> Unit,
    onMessageChanged: (String) -> Unit,
) {
    val action = rebaseLine.rebaseAction
    val isDrop = action == RebaseAction.DROP
    val focusRequester = remember { FocusRequester() }

    var newMessage by remember(rebaseLine.commit.name(), action) {
        if (action == RebaseAction.REWORD) {
            mutableStateOf(message ?: rebaseLine.shortMessage)
        } else
            mutableStateOf(rebaseLine.shortMessage)
    }

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .clickable { onFocusLine() }
            .backgroundIf(isSelected, MaterialTheme.colors.backgroundSelected)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Color bar on the left edge
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(action.color())
        )

        // Drag handle
        Text(
            text = "⠿",
            color = MaterialTheme.colors.onBackgroundSecondary.copy(alpha = 0.4f),
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        // Action chip
        ActionDropdown(
            action,
            isFirst = isFirst,
            onActionDropDownClicked = onFocusLine,
            onActionChanged = onActionChanged,
        )

        // Short hash
        Text(
            text = rebaseLine.commit.name().take(7),
            color = if (isDrop) MaterialTheme.colors.onBackgroundSecondary.copy(alpha = 0.3f)
                    else MaterialTheme.colors.primary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        // Commit message
        if (action == RebaseAction.REWORD) {
            AdjustableOutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 36.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.hasFocus && !isSelected) {
                            onFocusLine()
                        }
                    },
                enabled = true,
                value = newMessage,
                onValueChange = {
                    newMessage = it
                    onMessageChanged(it)
                },
                textStyle = MaterialTheme.typography.body2,
                backgroundColor = MaterialTheme.colors.background,
            )
        } else {
            Text(
                text = newMessage,
                color = if (isDrop) MaterialTheme.colors.onBackgroundSecondary.copy(alpha = 0.3f)
                        else MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.body2,
                textDecoration = if (isDrop) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
            )
        }
    }
}


@Composable
fun ActionDropdown(
    action: RebaseAction,
    isFirst: Boolean,
    onActionDropDownClicked: () -> Unit,
    onActionChanged: (RebaseAction) -> Unit,
) {
    var showDropDownMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(AppShapes.small)
            .background(action.color().copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = action.color().copy(alpha = 0.3f),
                shape = AppShapes.small,
            )
    ) {
        TextButton(
            onClick = {
                showDropDownMenu = true
                onActionDropDownClicked()
            },
            modifier = Modifier
                .width(100.dp)
                .height(32.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            Text(
                action.displayName,
                color = action.color(),
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )

            Icon(
                painterResource(Res.drawable.expand_more),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = action.color(),
            )
        }

        DropdownMenu(
            expanded = showDropDownMenu,
            onDismissRequest = { showDropDownMenu = false },
        ) {
            val dropDownItems = if (isFirst) {
                firstItemActions
            } else {
                actions
            }

            for (dropDownOption in dropDownItems) {
                DropdownMenuItem(
                    onClick = {
                        showDropDownMenu = false
                        onActionChanged(dropDownOption)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(AppShapes.small)
                            .background(dropDownOption.color())
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dropDownOption.displayName,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}

@Composable
private fun RebaseAction.color(): Color {
    return when (this) {
        RebaseAction.PICK -> MaterialTheme.colors.primary
        RebaseAction.REWORD -> Color(0xFF4438D0)
        RebaseAction.SQUASH -> Color(0xFFD88020)
        RebaseAction.FIXUP -> Color(0xFF2E8C48)
        RebaseAction.EDIT -> Color(0xFF7B74FF)
        RebaseAction.DROP -> MaterialTheme.colors.error
        RebaseAction.COMMENT -> MaterialTheme.colors.onBackgroundSecondary
    }
}

val firstItemActions = listOf(
    RebaseAction.PICK,
    RebaseAction.REWORD,
    RebaseAction.DROP,
)

val actions = listOf(
    RebaseAction.PICK,
    RebaseAction.REWORD,
    RebaseAction.SQUASH,
    RebaseAction.FIXUP,
    RebaseAction.EDIT,
    RebaseAction.DROP,
)
