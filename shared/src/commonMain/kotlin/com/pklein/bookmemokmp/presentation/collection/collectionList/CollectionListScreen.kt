package com.pklein.bookmemokmp.presentation.collection.collectionList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.item_count
import bookmemokmp.shared.generated.resources.search_all_empty
import bookmemokmp.shared.generated.resources.search_category_empty
import bookmemokmp.shared.generated.resources.search_empty
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.presentation.collection.filter.CollectionFilter
import com.pklein.bookmemokmp.presentation.collection.viewmodel.UpdateCheckState
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CollectionListPage(
    items: List<CollectionItem>,
    searchQuery: String,
    activeFilter: CollectionFilter,
    hasActiveStatusFilters: Boolean,
    listState: LazyListState,
    updateCheckState: UpdateCheckState,
    onEditClick: (CollectionItem) -> Unit,
    onFavoriteToggle: (CollectionItem) -> Unit,
    onProgressUpdate: (CollectionItem) -> Unit,
    onCheckForUpdates: (CollectionItem) -> Unit,
    onDismissUpdateCheck: () -> Unit,
) {
    val noActiveFilters = activeFilter == CollectionFilter.ALL && !hasActiveStatusFilters
    when {
        items.isEmpty() && searchQuery.isBlank() && noActiveFilters -> {
            EmptyState(stringResource(Res.string.search_all_empty))
        }

        items.isEmpty() && searchQuery.isBlank() -> {
            EmptyState(stringResource(Res.string.search_category_empty))
        }

        items.isEmpty() -> {
            EmptyState(stringResource(Res.string.search_empty, searchQuery))
        }

        else -> {
            Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                Text(
                    text = pluralStringResource(Res.plurals.item_count, items.size, items.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 88.dp, top = 4.dp),
                ) {
                    items(items, key = { it.id }) { item ->
                        BookItem(
                            item = item,
                            onFavoriteToggle = onFavoriteToggle,
                            onEditClick = onEditClick,
                            onProgressUpdate = onProgressUpdate,
                            updateCheckState = updateCheckState,
                            onCheckForUpdates = onCheckForUpdates,
                            onDismissUpdateCheck = onDismissUpdateCheck,
                        )
                    }
                }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

/** All filter selected, list with items. */
@Preview(showBackground = true)
@Composable
private fun PreviewCollectionWithItems() {
    BookMemoTheme {
        CollectionListPage(
            items = sampleItems,
            searchQuery = "",
            activeFilter = CollectionFilter.ALL,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewCollectionWithItemsBigFont() {
    BookMemoTheme {
        CollectionListPage(
            items = sampleItems,
            searchQuery = "",
            activeFilter = CollectionFilter.ALL,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}

private val sampleItems =
    listOf(
        CollectionItem(
            id = 1,
            type = ItemType.LITERATURE,
            title = "The Lord of the Rings",
            author = "J.R.R. Tolkien",
            description = "An epic high-fantasy novel.",
            bought = true,
            favorite = true,
            tome = 3,
            chapter = 42,
        ),
        CollectionItem(
            id = 2,
            type = ItemType.MANGA,
            title = "One Piece",
            author = "Eiichiro Oda",
            bought = true,
            tome = 107,
            chapter = 1100,
            episode = 1090,
        ),
        CollectionItem(
            id = 3,
            type = ItemType.COMIC,
            title = "Tintin au Tibet",
            author = "Hergé",
            finished = true,
        ),
    )

/** Search returned no results. */
@Preview(showBackground = true)
@Composable
private fun PreviewCollectionNoResults() {
    BookMemoTheme {
        CollectionListPage(
            items = emptyList(),
            searchQuery = "Dune",
            activeFilter = CollectionFilter.ALL,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}

/** Search returned no results — dark theme. */
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewCollectionNoResultsDark() {
    BookMemoTheme {
        CollectionListPage(
            items = emptyList(),
            searchQuery = "Dune",
            activeFilter = CollectionFilter.ALL,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}

/** Empty collection — first-launch state. */
@Preview(showBackground = true)
@Composable
private fun PreviewCollectionEmpty() {
    BookMemoTheme {
        CollectionListPage(
            items = emptyList(),
            searchQuery = "",
            activeFilter = CollectionFilter.ALL,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}

/** Empty collection — first-launch state. */
@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewCollectionEmptyBigFont() {
    BookMemoTheme {
        CollectionListPage(
            items = emptyList(),
            searchQuery = "",
            activeFilter = CollectionFilter.ALL,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}
