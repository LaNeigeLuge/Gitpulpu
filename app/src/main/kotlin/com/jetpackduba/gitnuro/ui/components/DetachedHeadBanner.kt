package com.jetpackduba.gitnuro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpackduba.gitnuro.app.generated.resources.Res
import com.jetpackduba.gitnuro.app.generated.resources.warning
import com.jetpackduba.gitnuro.domain.models.Branch
import com.jetpackduba.gitnuro.theme.AppShapes
import com.jetpackduba.gitnuro.theme.conflictFile
import org.jetbrains.compose.resources.painterResource

/**
 * Shown when HEAD is detached (checked out a commit/tag, or left by a rebase). New commits made
 * here don't belong to any branch and are easy to lose. Offers to either create a branch here or
 * move the current commit onto an existing branch.
 */
@Composable
fun DetachedHeadBanner(
    localBranches: List<Branch>,
    onCreateBranch: () -> Unit,
    onMoveToBranch: (Branch) -> Unit,
) {
    val accent = MaterialTheme.colors.conflictFile
    var showBranchMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(AppShapes.medium)
            .background(accent.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(Res.drawable.warning),
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(24.dp),
        )

        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
        ) {
            Text(
                text = "Detached HEAD — you're not on a branch",
                color = MaterialTheme.colors.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "New commits won't belong to any branch. Move this commit onto a branch, or create a new one here.",
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                fontSize = 12.sp,
            )
        }

        Box {
            PrimaryButton(
                text = "Move to branch ▾",
                onClick = { showBranchMenu = true },
                modifier = Modifier.padding(start = 8.dp),
            )

            DropdownMenu(
                expanded = showBranchMenu,
                onDismissRequest = { showBranchMenu = false },
            ) {
                if (localBranches.isEmpty()) {
                    DropdownMenuItem(onClick = { showBranchMenu = false }, enabled = false) {
                        Text("No local branches", style = MaterialTheme.typography.body2)
                    }
                } else {
                    for (branch in localBranches) {
                        DropdownMenuItem(onClick = {
                            showBranchMenu = false
                            onMoveToBranch(branch)
                        }) {
                            Text(branch.simpleName, style = MaterialTheme.typography.body2)
                        }
                    }
                }
            }
        }

        PrimaryButton(
            text = "Create branch here",
            onClick = onCreateBranch,
            backgroundColor = Color.Transparent,
            textColor = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}
