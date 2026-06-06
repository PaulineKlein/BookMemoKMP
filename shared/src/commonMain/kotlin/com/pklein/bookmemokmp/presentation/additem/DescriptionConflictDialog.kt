package com.pklein.bookmemokmp.presentation.additem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.desc_cancel
import bookmemokmp.shared.generated.resources.desc_concatenate
import bookmemokmp.shared.generated.resources.desc_conflict_message
import bookmemokmp.shared.generated.resources.desc_conflict_title
import bookmemokmp.shared.generated.resources.desc_replace
import org.jetbrains.compose.resources.stringResource

@Composable
fun DescriptionConflictDialog(
    onReplace: () -> Unit,
    onConcatenate: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(Res.string.desc_conflict_title)) },
        text = { Text(stringResource(Res.string.desc_conflict_message)) },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20),
                    onClick = onReplace
                ) { Text(stringResource(Res.string.desc_replace)) }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    onClick = onConcatenate
                ) { Text(stringResource(Res.string.desc_concatenate)) }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20),
                    colors = ButtonDefaults.outlinedButtonColors(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    onClick = onCancel
                ) { Text(stringResource(Res.string.desc_cancel)) }
            }

        },
        dismissButton = {}
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewDescriptionConflictDialog() {
    BookMemoTheme {
        DescriptionConflictDialog(onReplace = {}, onConcatenate = {}, onCancel = {})
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDescriptionConflictDialogBigFont() {
    BookMemoTheme {
        DescriptionConflictDialog(onReplace = {}, onConcatenate = {}, onCancel = {})
    }
}
