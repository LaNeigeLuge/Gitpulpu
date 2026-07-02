package com.jetpackduba.gitnuro.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpackduba.gitnuro.app.generated.resources.Res
import com.jetpackduba.gitnuro.app.generated.resources.generic_button_cancel
import com.jetpackduba.gitnuro.app.generated.resources.warning
import com.jetpackduba.gitnuro.theme.onBackgroundSecondary
import com.jetpackduba.gitnuro.ui.components.PrimaryButton
import com.jetpackduba.gitnuro.ui.dialogs.base.MaterialDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Confirms a force push before it happens — the destructive-action safety gate.
 * Force push rewrites the remote branch and can discard commits pushed by others.
 */
@Composable
fun ForcePushConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    MaterialDialog(onCloseRequested = onDismiss) {
        Column(modifier = Modifier.width(440.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(Res.drawable.warning),
                    contentDescription = null,
                    tint = MaterialTheme.colors.error,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Force push?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onBackground,
                )
            }

            Text(
                text = "This overwrites the remote branch with your local history. " +
                    "Any commits on the remote that you don't have locally will be lost.",
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(top = 16.dp),
            )

            Text(
                text = "Safe on your own feature branch. On a shared branch, pull first instead.",
                color = MaterialTheme.colors.onBackgroundSecondary,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(top = 8.dp),
            )

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 28.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PrimaryButton(
                    text = stringResource(Res.string.generic_button_cancel),
                    onClick = onDismiss,
                    backgroundColor = Color.Transparent,
                    textColor = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(end = 8.dp),
                )
                PrimaryButton(
                    text = "Force push",
                    onClick = onConfirm,
                    backgroundColor = MaterialTheme.colors.error,
                    textColor = MaterialTheme.colors.onError,
                )
            }
        }
    }
}
