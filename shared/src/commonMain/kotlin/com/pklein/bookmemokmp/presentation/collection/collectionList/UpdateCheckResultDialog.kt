package com.pklein.bookmemokmp.presentation.collection.collectionList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.cancel
import bookmemokmp.shared.generated.resources.check_update_error
import bookmemokmp.shared.generated.resources.check_update_new_episode
import bookmemokmp.shared.generated.resources.check_update_new_episode_no_progress
import bookmemokmp.shared.generated.resources.check_update_new_tome
import bookmemokmp.shared.generated.resources.check_update_new_tome_no_progress
import bookmemokmp.shared.generated.resources.check_update_up_to_date
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.presentation.collection.viewmodel.UpdateCheckState
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpdateCheckResultDialog(
    item: CollectionItem,
    state: UpdateCheckState,
    onDismiss: () -> Unit,
) {
    val message =
        when (state) {
            is UpdateCheckState.NewContent -> {
                if (state.newTotTome != null) {
                    if (state.userTome != null) {
                        stringResource(
                            Res.string.check_update_new_tome,
                            state.newTotTome,
                            state.userTome,
                        )
                    } else {
                        stringResource(Res.string.check_update_new_tome_no_progress, state.newTotTome)
                    }
                } else if (state.newTotEpisode != null) {
                    if (state.userEpisode != null) {
                        stringResource(
                            Res.string.check_update_new_episode,
                            state.newTotEpisode,
                            state.userEpisode,
                        )
                    } else {
                        stringResource(
                            Res.string.check_update_new_episode_no_progress,
                            state.newTotEpisode,
                        )
                    }
                } else {
                    ""
                }
            }

            is UpdateCheckState.UpToDate -> {
                stringResource(Res.string.check_update_up_to_date)
            }

            is UpdateCheckState.Error -> {
                stringResource(Res.string.check_update_error)
            }

            else -> {
                return
            }
        }
    UpdateCheckResultDialogContent(title = item.title, message = message, onDismiss = onDismiss)
}

@Composable
private fun UpdateCheckResultDialogContent(
    title: String,
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun PreviewUpdateCheckResultDialog() {
    BookMemoTheme {
        UpdateCheckResultDialogContent(
            title = "Naruto",
            message = "New volumes available! 72 volumes total (you're at tome 71)",
            onDismiss = {},
        )
    }
}

@Preview(fontScale = 2.0f)
@Composable
private fun PreviewUpdateCheckResultDialogBigFont() {
    BookMemoTheme {
        UpdateCheckResultDialogContent(
            title = "Naruto",
            message = "New volumes available! 72 volumes total (you're at tome 71)",
            onDismiss = {},
        )
    }
}
