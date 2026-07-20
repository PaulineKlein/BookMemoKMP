package com.pklein.bookmemokmp.presentation.collection.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.discover_empty
import bookmemokmp.shared.generated.resources.discover_error
import bookmemokmp.shared.generated.resources.discover_loading
import bookmemokmp.shared.generated.resources.discover_retry
import bookmemokmp.shared.generated.resources.close_accessibility
import bookmemokmp.shared.generated.resources.discover_title
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.presentation.collection.viewmodel.DiscoverState
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverBookBottomSheet(
    state: DiscoverState,
    author: String? = null,
    onDismiss: () -> Unit,
    onRetryTopManga: () -> Unit,
    onRetryAuthor: () -> Unit,
    onLoadMore: () -> Unit,
    onAddToWishlistTopManga: (SearchResult) -> Unit,
    onAddToWishlistAuthor: (SearchResult) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        DiscoverBookContent(
            state = state,
            author = author,
            onDismiss = onDismiss,
            onRetryTopManga = onRetryTopManga,
            onRetryAuthor = onRetryAuthor,
            onLoadMore = onLoadMore,
            onAddToWishlistTopManga = onAddToWishlistTopManga,
            onAddToWishlistAuthor = onAddToWishlistAuthor,
        )
    }
}

@Composable
private fun DiscoverBookContent(
    state: DiscoverState,
    author: String? = null,
    onDismiss: () -> Unit,
    onRetryTopManga: () -> Unit,
    onRetryAuthor: () -> Unit,
    onLoadMore: () -> Unit,
    onAddToWishlistTopManga: (SearchResult) -> Unit,
    onAddToWishlistAuthor: (SearchResult) -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (author.isNullOrBlank()) stringResource(Res.string.discover_title) else author,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(vertical = 12.dp),
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.close_accessibility),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        when (state) {
            is DiscoverState.Idle, is DiscoverState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(Res.string.discover_loading),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            is DiscoverState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.discover_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            is DiscoverState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.discover_error),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        TextButton(
                            onClick = {
                                if (author.isNullOrBlank()) {
                                    onRetryTopManga()
                                } else {
                                    onRetryAuthor()
                                }
                            },
                        ) {
                            Text(stringResource(Res.string.discover_retry))
                        }
                    }
                }
            }

            is DiscoverState.Success -> {
                val listState = rememberLazyListState()
                val reachedBottom =
                    remember {
                        derivedStateOf {
                            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                            val total = listState.layoutInfo.totalItemsCount
                            lastVisible != null && lastVisible.index >= total - 3
                        }
                    }
                LaunchedEffect(reachedBottom.value) {
                    if (reachedBottom.value) onLoadMore()
                }
                LazyColumn(state = listState) {
                    itemsIndexed(state.results, key = { index, _ -> index }) { _, result ->
                        DiscoverBookItem(
                            result = result,
                            onAddToWishlist = {
                                if (author.isNullOrBlank()) {
                                    onAddToWishlistTopManga(result)
                                } else {
                                    onAddToWishlistAuthor(result)
                                }
                            },
                        )
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val sampleResults =
    listOf(
        SearchResult(
            title = "Berserk",
            author = "Kentaro Miura",
            year = 1989,
            description = "A dark fantasy manga following Guts, a lone mercenary warrior.",
        ),
        SearchResult(
            title = "Vagabond",
            author = "Takehiko Inoue",
            year = 1998,
            description = "A fictionalized account of the life of Miyamoto Musashi.",
        ),
        SearchResult(
            title = "A Silent Voice",
            author = "Yoshitoki Oima",
            year = 2013,
            description = null,
        ),
    )

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverLoading() {
    BookMemoTheme {
        DiscoverBookContent(
            state = DiscoverState.Loading,
            onDismiss = {},
            onRetryTopManga = {},
            onRetryAuthor = {},
            onLoadMore = {},
            onAddToWishlistTopManga = {},
            onAddToWishlistAuthor = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverEmpty() {
    BookMemoTheme {
        DiscoverBookContent(
            state = DiscoverState.Empty,
            onDismiss = {},
            onRetryTopManga = {},
            onRetryAuthor = {},
            onLoadMore = {},
            onAddToWishlistTopManga = {},
            onAddToWishlistAuthor = {},
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDiscoverEmptyBigFont() {
    BookMemoTheme {
        DiscoverBookContent(
            state = DiscoverState.Empty,
            onDismiss = {},
            onRetryTopManga = {},
            onRetryAuthor = {},
            onLoadMore = {},
            onAddToWishlistTopManga = {},
            onAddToWishlistAuthor = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverError() {
    BookMemoTheme {
        DiscoverBookContent(
            state = DiscoverState.Error,
            onDismiss = {},
            onRetryTopManga = {},
            onRetryAuthor = {},
            onLoadMore = {},
            onAddToWishlistTopManga = {},
            onAddToWishlistAuthor = {},
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDiscoverErrorBigFont() {
    BookMemoTheme {
        DiscoverBookContent(
            state = DiscoverState.Error,
            onDismiss = {},
            onRetryTopManga = {},
            onRetryAuthor = {},
            onLoadMore = {},
            onAddToWishlistTopManga = {},
            onAddToWishlistAuthor = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverSuccess() {
    BookMemoTheme {
        DiscoverBookContent(
            state = DiscoverState.Success(sampleResults),
            onDismiss = {},
            onRetryTopManga = {},
            onRetryAuthor = {},
            onLoadMore = {},
            onAddToWishlistTopManga = {},
            onAddToWishlistAuthor = {},
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDiscoverSuccessBigFont() {
    BookMemoTheme {
        DiscoverBookContent(
            state = DiscoverState.Success(sampleResults),
            onDismiss = {},
            onRetryTopManga = {},
            onRetryAuthor = {},
            onLoadMore = {},
            onAddToWishlistTopManga = {},
            onAddToWishlistAuthor = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverAuthorSuccess() {
    BookMemoTheme {
        DiscoverBookContent(
            state = DiscoverState.Success(sampleResults),
            author = "Kentaro Miura",
            onDismiss = {},
            onRetryTopManga = {},
            onRetryAuthor = {},
            onLoadMore = {},
            onAddToWishlistTopManga = {},
            onAddToWishlistAuthor = {},
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDiscoverAuthorSuccessBigFont() {
    BookMemoTheme {
        DiscoverBookContent(
            state = DiscoverState.Success(sampleResults),
            author = "Kentaro Miura",
            onDismiss = {},
            onRetryTopManga = {},
            onRetryAuthor = {},
            onLoadMore = {},
            onAddToWishlistTopManga = {},
            onAddToWishlistAuthor = {},
        )
    }
}
