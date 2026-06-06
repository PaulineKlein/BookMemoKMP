package com.pklein.bookmemokmp.presentation.collection.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import bookmemokmp.shared.generated.resources.discover_error
import bookmemokmp.shared.generated.resources.discover_loading
import bookmemokmp.shared.generated.resources.discover_retry
import bookmemokmp.shared.generated.resources.discover_title
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.presentation.collection.viewmodel.DiscoverState
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverMangaBottomSheet(
    state: DiscoverState,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onAddToWishlist: (SearchResult) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        DiscoverSheetContent(
            state = state,
            onRetry = onRetry,
            onLoadMore = onLoadMore,
            onAddToWishlist = onAddToWishlist
        )
    }
}

@Composable
private fun DiscoverSheetContent(
    state: DiscoverState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onAddToWishlist: (SearchResult) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = stringResource(Res.string.discover_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        when (state) {
            is DiscoverState.Idle, is DiscoverState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(Res.string.discover_loading),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is DiscoverState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.discover_error),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        TextButton(onClick = onRetry) {
                            Text(stringResource(Res.string.discover_retry))
                        }
                    }
                }
            }

            is DiscoverState.Success -> {
                val listState = rememberLazyListState()
                val reachedBottom = remember {
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
                    items(state.results, key = { it.title }) { result ->
                        DiscoverMangaItem(
                            result = result,
                            onAddToWishlist = { onAddToWishlist(result) }
                        )
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
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

private val sampleResults = listOf(
    SearchResult(
        title = "Berserk",
        author = "Kentaro Miura",
        year = 1989,
        description = "A dark fantasy manga following Guts, a lone mercenary warrior."
    ),
    SearchResult(
        title = "Vagabond",
        author = "Takehiko Inoue",
        year = 1998,
        description = "A fictionalized account of the life of Miyamoto Musashi."
    ),
    SearchResult(
        title = "A Silent Voice",
        author = "Yoshitoki Oima",
        year = 2013,
        description = null
    )
)

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverLoading() {
    BookMemoTheme {
        DiscoverSheetContent(
            state = DiscoverState.Loading,
            onRetry = {},
            onLoadMore = {},
            onAddToWishlist = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverError() {
    BookMemoTheme {
        DiscoverSheetContent(
            state = DiscoverState.Error,
            onRetry = {},
            onLoadMore = {},
            onAddToWishlist = {}
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDiscoverErrorBigFont() {
    BookMemoTheme {
        DiscoverSheetContent(
            state = DiscoverState.Error,
            onRetry = {},
            onLoadMore = {},
            onAddToWishlist = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverSuccess() {
    BookMemoTheme {
        DiscoverSheetContent(
            state = DiscoverState.Success(sampleResults),
            onRetry = {},
            onLoadMore = {},
            onAddToWishlist = {}
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDiscoverSuccessBigFont() {
    BookMemoTheme {
        DiscoverSheetContent(
            state = DiscoverState.Success(sampleResults),
            onRetry = {},
            onLoadMore = {},
            onAddToWishlist = {}
        )
    }
}
