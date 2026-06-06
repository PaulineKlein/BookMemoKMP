package com.pklein.bookmemokmp.presentation.additem

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.add_item
import bookmemokmp.shared.generated.resources.cancel
import bookmemokmp.shared.generated.resources.duplicate_title_dialog_message
import bookmemokmp.shared.generated.resources.duplicate_title_dialog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun DuplicateWarningDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.duplicate_title_dialog_title)) },
        text = { Text(stringResource(Res.string.duplicate_title_dialog_message)) },
        confirmButton = {
            Button(onClick = onConfirm) { Text(stringResource(Res.string.add_item)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } }
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewDuplicateWarningDialog() {
    BookMemoTheme {
        DuplicateWarningDialog(onConfirm = {}, onDismiss = {})
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDuplicateWarningDialogBigFont() {
    BookMemoTheme {
        DuplicateWarningDialog(onConfirm = {}, onDismiss = {})
    }
}
