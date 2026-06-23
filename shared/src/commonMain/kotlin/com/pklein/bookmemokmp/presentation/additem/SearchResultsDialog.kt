package com.pklein.bookmemokmp.presentation.additem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.cancel
import bookmemokmp.shared.generated.resources.result_dialog_notice
import bookmemokmp.shared.generated.resources.result_dialog_title
import bookmemokmp.shared.generated.resources.result_dialog_toggle
import bookmemokmp.shared.generated.resources.searching_error
import bookmemokmp.shared.generated.resources.searching_no_result
import bookmemokmp.shared.generated.resources.searching_not_found_exception
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.presentation.additem.viewmodel.SearchState
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchResultsDialog(
    searchState: SearchState,
    query: String?,
    showEnglishNotice: Boolean = false,
    initialSaveDescription: Boolean = true,
    onSelect: (SearchResult, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var shouldSaveDescription by remember { mutableStateOf(initialSaveDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.result_dialog_title)) },
        text = {
            when (searchState) {
                SearchState.Idle, SearchState.Loading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                SearchState.Empty -> {
                    Text(
                        text =
                            stringResource(
                                Res.string.searching_no_result,
                                (query?.trim()) ?: "",
                            ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                SearchState.NotFoundException -> {
                    Text(
                        text = stringResource(Res.string.searching_not_found_exception),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                SearchState.Error -> {
                    Text(
                        text = stringResource(Res.string.searching_error),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                else -> {
                    val results = (searchState as? SearchState.Success)?.results
                    if (results.isNullOrEmpty()) {
                        Text(
                            text = stringResource(Res.string.searching_error),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        return@AlertDialog
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (showEnglishNotice) {
                            item {
                                Text(
                                    text = stringResource(Res.string.result_dialog_notice),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 12.dp),
                                )
                                ToggleRowItem(
                                    label = stringResource(Res.string.result_dialog_toggle),
                                    checked = shouldSaveDescription,
                                    onCheckedChange = { shouldSaveDescription = it },
                                )
                                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                        items(results) { result ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelect(
                                                result,
                                                if (showEnglishNotice) shouldSaveDescription else true,
                                            )
                                        }.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                CoverPreviewItem(
                                    imageUrl = result.imageUrl ?: "",
                                    modifier =
                                        Modifier
                                            .size(40.dp, 56.dp),
                                )
                                Column {
                                    Text(
                                        text = result.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    if (result.author != null) {
                                        Text(
                                            text = result.author,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    if (result.year != null) {
                                        Text(
                                            text = result.year.toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewResults =
    listOf(
        SearchResult(
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            year = 1937,
            description = null,
        ),
        SearchResult(title = "Berserk", author = "Kentaro Miura", year = 1989, description = null),
        SearchResult(title = "Watchmen", author = null, year = null, description = null),
    )

@Preview(showBackground = true, name = "loading state")
@Composable
private fun PreviewSearchResultsDialogLoading() {
    BookMemoTheme {
        SearchResultsDialog(
            searchState = SearchState.Loading,
            query = "hobbit",
            onSelect = { _, _ -> },
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSearchResultsDialog() {
    BookMemoTheme {
        SearchResultsDialog(
            searchState = SearchState.Success(previewResults),
            query = "hobbit",
            onSelect = { _, _ -> },
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSearchInFrenchResultsDialog() {
    BookMemoTheme {
        SearchResultsDialog(
            searchState = SearchState.Success(previewResults),
            query = "hobbit",
            showEnglishNotice = true,
            onSelect = { _, _ -> },
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewSearchResultsDialogBigFont() {
    BookMemoTheme {
        SearchResultsDialog(
            searchState = SearchState.Success(previewResults),
            query = "hobbit",
            showEnglishNotice = true,
            onSelect = { _, _ -> },
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "empty state")
@Composable
private fun PreviewSearchResultsDialogEmpty() {
    BookMemoTheme {
        SearchResultsDialog(
            searchState = SearchState.Empty,
            query = "hobbit",
            onSelect = { _, _ -> },
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "error state")
@Composable
private fun PreviewSearchResultsDialogError() {
    BookMemoTheme {
        SearchResultsDialog(
            searchState = SearchState.Error,
            query = "hobbit",
            onSelect = { _, _ -> },
            onDismiss = {},
        )
    }
}
