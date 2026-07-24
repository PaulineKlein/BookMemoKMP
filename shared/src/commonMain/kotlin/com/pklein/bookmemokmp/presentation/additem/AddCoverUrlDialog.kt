package com.pklein.bookmemokmp.presentation.additem

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.cancel
import bookmemokmp.shared.generated.resources.cover_url_dialog_title
import bookmemokmp.shared.generated.resources.save
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddCoverUrlDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var url by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.cover_url_dialog_title)) },
        text = {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(url.trim()) }) {
                Text(stringResource(Res.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewAddCoverUrlDialog() {
    BookMemoTheme {
        AddCoverUrlDialog(onConfirm = {}, onDismiss = {})
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewAddCoverUrlDialogBigFont() {
    BookMemoTheme {
        AddCoverUrlDialog(onConfirm = {}, onDismiss = {})
    }
}
