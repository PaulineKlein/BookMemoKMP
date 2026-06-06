package com.pklein.bookmemokmp.presentation.collection.collectionList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.add_number
import bookmemokmp.shared.generated.resources.cancel
import bookmemokmp.shared.generated.resources.last_chapter
import bookmemokmp.shared.generated.resources.last_episode
import bookmemokmp.shared.generated.resources.last_season
import bookmemokmp.shared.generated.resources.last_volume
import bookmemokmp.shared.generated.resources.save
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProgressDialog(
    item: CollectionItem,
    onDismiss: () -> Unit,
    onConfirm: (CollectionItem) -> Unit,
) {
    var tome by remember { mutableStateOf(item.tome?.toString() ?: "") }
    var chapter by remember { mutableStateOf(item.chapter?.toString() ?: "") }
    var episode by remember { mutableStateOf(item.episode?.toString() ?: "") }
    var season by remember { mutableStateOf(item.season?.toString() ?: "") }
    var title by remember { mutableStateOf(item.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ProgressFieldRow(
                    value = tome,
                    onValueChange = { tome = it },
                    label = stringResource(Res.string.last_volume),
                )
                ProgressFieldRow(
                    value = chapter,
                    onValueChange = { chapter = it },
                    label = stringResource(Res.string.last_chapter),
                )
                ProgressFieldRow(
                    value = season,
                    onValueChange = { season = it },
                    label = stringResource(Res.string.last_season),
                )
                ProgressFieldRow(
                    value = episode,
                    onValueChange = { episode = it },
                    label = stringResource(Res.string.last_episode),
                )
            }
        },
        confirmButton = {
            Button(
                shape = RoundedCornerShape(20),
                onClick = {
                    onConfirm(
                        item.copy(
                            tome = tome.toIntOrNull(),
                            chapter = chapter.toIntOrNull(),
                            episode = episode.toIntOrNull(),
                            season = season.toIntOrNull(),
                        ),
                    )
                },
            ) { Text(stringResource(Res.string.save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
    )
}

@Composable
private fun ProgressFieldRow(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.all(Char::isDigit)) onValueChange(it) },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = {
            val next = (value.toIntOrNull() ?: 0) + 1
            onValueChange(next.toString())
        }) {
            Text(
                stringResource(Res.string.add_number),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

/** Progress dialog — pre-filled manga with all three fields. */
@Preview
@Composable
private fun PreviewProgressDialog() {
    BookMemoTheme {
        ProgressDialog(
            item =
                CollectionItem(
                    id = 2,
                    type = ItemType.MANGA,
                    title = "One Piece",
                    tome = 107,
                    chapter = 1100,
                    episode = 1090,
                    season = 20,
                ),
            onDismiss = {},
            onConfirm = {},
        )
    }
}

/** Progress dialog — empty fields, +1 starts from 0. */
@Preview
@Composable
private fun PreviewProgressDialogEmpty() {
    BookMemoTheme {
        ProgressDialog(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.MANGA,
                    title = "Naruto",
                ),
            onDismiss = {},
            onConfirm = {},
        )
    }
}

/** Progress dialog — empty fields, +1 starts from 0. */
@Preview(fontScale = 2.0f)
@Composable
private fun PreviewProgressDialogEmptyBigFont() {
    BookMemoTheme {
        ProgressDialog(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.MANGA,
                    title = "Naruto",
                ),
            onDismiss = {},
            onConfirm = {},
        )
    }
}
