package com.pklein.bookmemokmp.presentation.additem

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.cancel
import bookmemokmp.shared.generated.resources.delete
import bookmemokmp.shared.generated.resources.delete_item_message
import bookmemokmp.shared.generated.resources.delete_item_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.delete_item_title)) },
        text = { Text(stringResource(Res.string.delete_item_message)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text(stringResource(Res.string.delete)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } }
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewDeleteConfirmationDialog() {
    BookMemoTheme {
        DeleteConfirmationDialog(onConfirm = {}, onDismiss = {})
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDeleteConfirmationDialogBigFont() {
    BookMemoTheme {
        DeleteConfirmationDialog(onConfirm = {}, onDismiss = {})
    }
}