package com.pklein.bookmemokmp.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.cancel
import bookmemokmp.shared.generated.resources.settings_restore_conflict_message
import bookmemokmp.shared.generated.resources.settings_restore_conflict_title
import bookmemokmp.shared.generated.resources.settings_restore_duplicate
import bookmemokmp.shared.generated.resources.settings_restore_replace
import bookmemokmp.shared.generated.resources.settings_restore_skip
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.presentation.settings.viewmodel.RestoreConflict
import com.pklein.bookmemokmp.presentation.settings.viewmodel.RestoreStrategy
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestoreConflictDialog(
    conflict: RestoreConflict,
    onApplyRestore: (RestoreStrategy) -> Unit,
    onDismiss: () -> Unit,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.settings_restore_conflict_title),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text =
                        stringResource(
                            Res.string.settings_restore_conflict_message,
                            conflict.duplicateCount,
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Button(
                        onClick = { onApplyRestore(RestoreStrategy.REPLACE) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(Res.string.settings_restore_replace))
                    }
                    TextButton(
                        onClick = { onApplyRestore(RestoreStrategy.DUPLICATE) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(Res.string.settings_restore_duplicate))
                    }
                    TextButton(
                        onClick = { onApplyRestore(RestoreStrategy.SKIP) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(Res.string.settings_restore_skip))
                    }
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val sampleConflict =
    RestoreConflict(
        duplicateCount = 3,
        pendingItems =
            listOf(
                CollectionItem(type = ItemType.LITERATURE, title = "Dune"),
                CollectionItem(type = ItemType.MANGA, title = "One Piece"),
                CollectionItem(type = ItemType.COMIC, title = "Tintin"),
            ),
    )

@Preview
@Composable
private fun PreviewRestoreConflictDialog() {
    BookMemoTheme {
        RestoreConflictDialog(
            conflict = sampleConflict,
            onApplyRestore = {},
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun PreviewRestoreConflictDialogSingle() {
    BookMemoTheme {
        RestoreConflictDialog(
            conflict =
                RestoreConflict(
                    duplicateCount = 1,
                    pendingItems = listOf(CollectionItem(type = ItemType.LITERATURE, title = "Dune")),
                ),
            onApplyRestore = {},
            onDismiss = {},
        )
    }
}
