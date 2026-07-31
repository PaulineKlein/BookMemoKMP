package com.pklein.bookmemokmp.presentation.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.cancel
import bookmemokmp.shared.generated.resources.settings_delete_cloud
import bookmemokmp.shared.generated.resources.settings_delete_cloud_confirm_message
import bookmemokmp.shared.generated.resources.settings_delete_cloud_confirm_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteCloudBackupDialog(
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.settings_delete_cloud_confirm_title),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.settings_delete_cloud_confirm_message),
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDelete,
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
            ) {
                Text(stringResource(Res.string.settings_delete_cloud))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
    )
}
