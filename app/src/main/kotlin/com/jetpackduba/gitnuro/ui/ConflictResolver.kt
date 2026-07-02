package com.jetpackduba.gitnuro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpackduba.gitnuro.domain.ConflictParser
import com.jetpackduba.gitnuro.domain.models.ConflictBlock
import com.jetpackduba.gitnuro.domain.models.ConflictChoice
import com.jetpackduba.gitnuro.domain.models.ConflictedFile
import com.jetpackduba.gitnuro.theme.AppShapes
import com.jetpackduba.gitnuro.theme.addFile
import com.jetpackduba.gitnuro.theme.onBackgroundSecondary
import com.jetpackduba.gitnuro.ui.components.PrimaryButton
import com.jetpackduba.gitnuro.ui.components.ScrollableLazyColumn

/**
 * Line-level conflict resolver. Each conflicting region shows "ours" and "theirs" side by side
 * with accept buttons (this side, or both). Unchanged context is dimmed. When every conflict has
 * a choice, "Save & mark resolved" writes the merged file and stages it.
 */
@Composable
fun ConflictResolver(
    conflictedFile: ConflictedFile,
    onSave: (resolvedContent: String) -> Unit,
    onUseAllOurs: () -> Unit,
    onUseAllTheirs: () -> Unit,
    onClose: () -> Unit,
) {
    // Index of each Conflict block (in order) -> chosen side
    val choices = remember(conflictedFile.filePath) { mutableStateMapOf<Int, ConflictChoice>() }

    val allResolved = choices.size == conflictedFile.conflictCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Resolve conflicts",
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${conflictedFile.filePath}  ·  ${choices.size}/${conflictedFile.conflictCount} resolved",
                    color = MaterialTheme.colors.onBackgroundSecondary,
                    fontSize = 12.sp,
                )
            }

            PrimaryButton(
                text = "Use all ours",
                onClick = onUseAllOurs,
                backgroundColor = Color.Transparent,
                textColor = MaterialTheme.colors.primary,
            )
            PrimaryButton(
                text = "Use all theirs",
                onClick = onUseAllTheirs,
                backgroundColor = Color.Transparent,
                textColor = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.08f))

        // Blocks
        var conflictIndex = -1
        val indexed = conflictedFile.blocks.map { block ->
            if (block is ConflictBlock.Conflict) conflictIndex++
            block to (if (block is ConflictBlock.Conflict) conflictIndex else -1)
        }

        ScrollableLazyColumn(modifier = Modifier.weight(1f)) {
            items(indexed.size) { i ->
                val (block, idx) = indexed[i]
                when (block) {
                    is ConflictBlock.Unchanged -> UnchangedRegion(block)
                    is ConflictBlock.Conflict -> ConflictRegion(
                        block = block,
                        chosen = choices[idx],
                        onChoose = { choice -> choices[idx] = choice },
                    )
                }
            }
        }

        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.08f))

        // Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Cancel",
                onClick = onClose,
                backgroundColor = Color.Transparent,
                textColor = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(end = 8.dp),
            )

            PrimaryButton(
                text = if (allResolved) "Save & mark resolved" else "Resolve all ${conflictedFile.conflictCount} conflicts",
                onClick = {
                    val resolved = ConflictParser.resolve(conflictedFile, choices.toMap())
                    onSave(resolved)
                },
                enabled = allResolved,
            )
        }
    }
}

@Composable
private fun UnchangedRegion(block: ConflictBlock.Unchanged) {
    if (block.lines.all { it.isEmpty() }) return

    val scroll = rememberScrollState()
    Text(
        text = block.lines.joinToString("\n"),
        color = MaterialTheme.colors.onBackgroundSecondary,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll)
            .padding(horizontal = 16.dp, vertical = 4.dp),
    )
}

@Composable
private fun ConflictRegion(
    block: ConflictBlock.Conflict,
    chosen: ConflictChoice?,
    onChoose: (ConflictChoice) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(AppShapes.small)
            .border(
                1.dp,
                MaterialTheme.colors.onBackground.copy(alpha = 0.12f),
                AppShapes.small,
            )
    ) {
        SidePane(
            label = "OURS (current)",
            accentColor = MaterialTheme.colors.primary,
            lines = block.ours,
            selected = chosen == ConflictChoice.OURS,
            onAccept = { onChoose(ConflictChoice.OURS) },
        )

        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.08f))

        SidePane(
            label = "THEIRS (incoming)",
            accentColor = MaterialTheme.colors.secondary,
            lines = block.theirs,
            selected = chosen == ConflictChoice.THEIRS,
            onAccept = { onChoose(ConflictChoice.THEIRS) },
        )

        // Keep-both row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = when (chosen) {
                    ConflictChoice.OURS -> "✓ keeping ours"
                    ConflictChoice.THEIRS -> "✓ keeping theirs"
                    ConflictChoice.OURS_THEN_THEIRS -> "✓ keeping both (ours first)"
                    ConflictChoice.THEIRS_THEN_OURS -> "✓ keeping both (theirs first)"
                    null -> "Choose a side"
                },
                color = if (chosen != null) MaterialTheme.colors.addFile else MaterialTheme.colors.error,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { onChoose(ConflictChoice.OURS_THEN_THEIRS) }) {
                Text("Keep both", fontSize = 12.sp, color = MaterialTheme.colors.onBackground)
            }
        }
    }
}

@Composable
private fun SidePane(
    label: String,
    accentColor: Color,
    lines: List<String>,
    selected: Boolean,
    onAccept: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) accentColor.copy(alpha = 0.15f) else Color.Transparent
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(AppShapes.small)
                    .background(accentColor)
            )
            Text(
                text = label,
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .weight(1f),
            )
            TextButton(onClick = onAccept) {
                Text(
                    if (selected) "✓ accepted" else "Accept this side",
                    fontSize = 11.sp,
                    color = accentColor,
                )
            }
        }

        val scroll = rememberScrollState()
        Text(
            text = lines.joinToString("\n").ifEmpty { "(empty)" },
            color = MaterialTheme.colors.onBackground,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 2.dp),
        )
    }
}
