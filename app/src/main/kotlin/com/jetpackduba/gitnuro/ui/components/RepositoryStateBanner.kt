package com.jetpackduba.gitnuro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpackduba.gitnuro.app.generated.resources.Res
import com.jetpackduba.gitnuro.app.generated.resources.warning
import com.jetpackduba.gitnuro.domain.extensions.isCherryPicking
import com.jetpackduba.gitnuro.domain.extensions.isMerging
import com.jetpackduba.gitnuro.domain.extensions.isReverting
import com.jetpackduba.gitnuro.theme.AppShapes
import com.jetpackduba.gitnuro.theme.conflictFile
import org.eclipse.jgit.lib.RepositoryState
import org.jetbrains.compose.resources.painterResource

/**
 * Full-width banner shown while the repository is mid-operation (rebase, merge,
 * cherry-pick, revert). Inspired by Fork's notification bar: the state is always
 * visible with its escape hatches, so the user is never stuck wondering what to do.
 */
@Composable
fun RepositoryStateBanner(
    repositoryState: RepositoryState,
    onAbort: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    val (title, description, showSkip) = when {
        repositoryState.isRebasing -> Triple(
            "Rebase in progress",
            "Resolve any conflicts, stage the files, then continue. Abort returns the branch to its state before the rebase.",
            true,
        )

        repositoryState.isMerging -> Triple(
            "Merge in progress",
            "Resolve any conflicts, stage the files, then commit to complete the merge. Abort cancels the merge.",
            false,
        )

        repositoryState.isCherryPicking -> Triple(
            "Cherry-pick in progress",
            "Resolve any conflicts, stage the files, then commit. Abort cancels the cherry-pick.",
            false,
        )

        repositoryState.isReverting -> Triple(
            "Revert in progress",
            "Resolve any conflicts, stage the files, then commit. Abort cancels the revert.",
            false,
        )

        else -> Triple(
            "Repository is in a special state (${repositoryState.description})",
            "Complete or abort the pending operation to get back to normal.",
            false,
        )
    }

    val accent = MaterialTheme.colors.conflictFile

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
                text = title,
                color = MaterialTheme.colors.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = description,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                fontSize = 12.sp,
            )
        }

        PrimaryButton(
            text = "Abort",
            onClick = onAbort,
            backgroundColor = Color.Transparent,
            textColor = MaterialTheme.colors.error,
            modifier = Modifier.padding(start = 8.dp),
        )

        if (showSkip) {
            PrimaryButton(
                text = "Skip",
                onClick = onSkip,
                backgroundColor = Color.Transparent,
                textColor = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(start = 4.dp),
            )

            PrimaryButton(
                text = "Continue",
                onClick = onContinue,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}
