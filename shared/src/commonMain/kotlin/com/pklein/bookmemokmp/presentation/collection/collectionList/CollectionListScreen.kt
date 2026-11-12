package com.pklein.bookmemokmp.presentation.collection.collectionList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.FilterListOff
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.add_item_menu
import bookmemokmp.shared.generated.resources.empty_category_description
import bookmemokmp.shared.generated.resources.empty_collection_action
import bookmemokmp.shared.generated.resources.empty_collection_description
import bookmemokmp.shared.generated.resources.empty_collection_title
import bookmemokmp.shared.generated.resources.empty_filters_action
import bookmemokmp.shared.generated.resources.empty_filters_description
import bookmemokmp.shared.generated.resources.empty_filters_title
import bookmemokmp.shared.generated.resources.empty_search_action
import bookmemokmp.shared.generated.resources.empty_search_description
import bookmemokmp.shared.generated.resources.item_count
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
    activeFilter: CollectionFilter?,
    hasActiveStatusFilters: Boolean,
    listState: LazyListState,
    updateCheckState: UpdateCheckState,
    onEditClick: (CollectionItem) -> Unit,
    onFavoriteToggle: (CollectionItem) -> Unit,
    onProgressUpdate: (CollectionItem) -> Unit,
    onCheckForUpdates: (CollectionItem) -> Unit,
    onSearchAuthor: (CollectionItem) -> Unit,
    onDismissUpdateCheck: () -> Unit,
    onAddClick: () -> Unit,
    onClearSearch: () -> Unit,
    onClearFilters: () -> Unit,
    onShare: ((CollectionItem) -> Unit)? = null,
) {
    val noActiveFilters = activeFilter == null && !hasActiveStatusFilters
    when {
        items.isEmpty() && searchQuery.isBlank() && noActiveFilters -> {
            EmptyState(
                title = stringResource(Res.string.empty_collection_title),
                description = stringResource(Res.string.empty_collection_description),
                illustration = { EmptyStateIcon(Icons.Outlined.AutoStories) },
                action = {
                    FilledTonalButton(
                        onClick = onAddClick,
                        shape = FloatingActionButtonDefaults.shape,
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(stringResource(Res.string.empty_collection_action))
                    }
                },
            )
        }

        items.isEmpty() && searchQuery.isBlank() && hasActiveStatusFilters -> {
            EmptyState(
                title = stringResource(Res.string.empty_filters_title),
                description = stringResource(Res.string.empty_filters_description),
                illustration = { EmptyStateIcon(Icons.Outlined.FilterListOff) },
                action = {
                    FilledTonalButton(onClick = onClearFilters) {
                        Text(stringResource(Res.string.empty_filters_action))
                    }
                },
            )
        }

        items.isEmpty() && searchQuery.isBlank() -> {
            EmptyState(
                title = stringResource(Res.string.search_category_empty),
                description = stringResource(Res.string.empty_category_description),
                illustration = { EmptyStateIcon(Icons.Outlined.AutoStories) },
                action = {
                    FilledTonalButton(
                        onClick = onAddClick,
                        shape = FloatingActionButtonDefaults.shape,
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(stringResource(Res.string.add_item_menu))
                    }
                },
            )
        }

        items.isEmpty() -> {
            EmptyState(
                title = stringResource(Res.string.search_empty, searchQuery),
                description = stringResource(Res.string.empty_search_description),
                illustration = { EmptyStateIcon(Icons.Outlined.SearchOff) },
                action = {
                    TextButton(onClick = onClearSearch) {
                        Text(stringResource(Res.string.empty_search_action))
                    }
                },
            )
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
                            onSearchAuthor = onSearchAuthor,
                            onShare = onShare,
                        )
                    }
                }
            }
        }
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
            activeFilter = null,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = {},
            onAddClick = {},
            onClearSearch = {},
            onClearFilters = {},
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
            activeFilter = null,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = {},
            onAddClick = {},
            onClearSearch = {},
            onClearFilters = {},
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
            activeFilter = null,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = {},
            onAddClick = {},
            onClearSearch = {},
            onClearFilters = {},
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
            activeFilter = null,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = {},
            onAddClick = {},
            onClearSearch = {},
            onClearFilters = {},
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
            activeFilter = null,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = {},
            onAddClick = {},
            onClearSearch = {},
            onClearFilters = {},
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
            activeFilter = null,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = {},
            onAddClick = {},
            onClearSearch = {},
            onClearFilters = {},
        )
    }
}

/** Status filters active, nothing matches. */
@Preview(showBackground = true)
@Composable
private fun PreviewCollectionNoFilterMatch() {
    BookMemoTheme {
        CollectionListPage(
            items = emptyList(),
            searchQuery = "",
            activeFilter = null,
            hasActiveStatusFilters = true,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = {},
            onAddClick = {},
            onClearSearch = {},
            onClearFilters = {},
        )
    }
}

/** Type filter active, no item of that type yet — dark theme, big font. */
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewCollectionCategoryEmptyDark() {
    BookMemoTheme {
        CollectionListPage(
            items = emptyList(),
            searchQuery = "",
            activeFilter = CollectionFilter.MANGA,
            hasActiveStatusFilters = false,
            listState = LazyListState(),
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = {},
            onAddClick = {},
            onClearSearch = {},
            onClearFilters = {},
        )
    }
}
