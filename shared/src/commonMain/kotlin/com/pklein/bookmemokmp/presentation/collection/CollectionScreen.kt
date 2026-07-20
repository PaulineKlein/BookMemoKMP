package com.pklein.bookmemokmp.presentation.collection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.add_accessibility
import bookmemokmp.shared.generated.resources.cat
import bookmemokmp.shared.generated.resources.clear_search_accessibility
import bookmemokmp.shared.generated.resources.csv_headline_title
import bookmemokmp.shared.generated.resources.search_placeholder
import bookmemokmp.shared.generated.resources.tab_collection
import bookmemokmp.shared.generated.resources.tab_stats
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.FormatType
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.presentation.collection.collectionList.CollectionListPage
import com.pklein.bookmemokmp.presentation.collection.discover.DiscoverMangaBottomSheet
import com.pklein.bookmemokmp.presentation.collection.filter.CollectionFilter
import com.pklein.bookmemokmp.presentation.collection.filter.FilterRow
import com.pklein.bookmemokmp.presentation.collection.filter.FormatFilterRow
import com.pklein.bookmemokmp.presentation.collection.filter.StatusFilterField
import com.pklein.bookmemokmp.presentation.collection.filter.StatusFilters
import com.pklein.bookmemokmp.presentation.collection.filter.SubFilterRow
import com.pklein.bookmemokmp.presentation.collection.filter.TriState
import com.pklein.bookmemokmp.presentation.collection.menu.MenuItem
import com.pklein.bookmemokmp.presentation.collection.statistics.StatisticsScreen
import com.pklein.bookmemokmp.presentation.collection.viewmodel.CollectionViewModel
import com.pklein.bookmemokmp.presentation.collection.viewmodel.DiscoverState
import com.pklein.bookmemokmp.presentation.collection.viewmodel.UpdateCheckState
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    onAddClick: () -> Unit,
    onEditClick: (CollectionItem) -> Unit,
    onExportCsv: (String) -> Unit = {},
    onExportDb: () -> Unit = {},
    onImportDb: () -> Unit = {},
) {
    val items by viewModel.displayedItems.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val statusFilters by viewModel.statusFilters.collectAsState()
    val formatFilter by viewModel.formatFilter.collectAsState()
    val discoverState by viewModel.discoverState.collectAsState()
    val updateCheckState by viewModel.updateCheckState.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current
    val csvHeader = stringResource(Res.string.csv_headline_title)

    CollectionContent(
        items = items,
        allItems = allItems,
        searchQuery = searchQuery,
        activeFilter = filter,
        activeStatusFilters = statusFilters,
        activeFormat = formatFilter,
        discoverState = discoverState,
        updateCheckState = updateCheckState,
        onSearchChange = viewModel::onSearchQueryChange,
        onClearSearch = {
            viewModel.clearSearch()
            keyboard?.hide()
        },
        onFilterChange = viewModel::onFilterChange,
        onStatusFilterCycle = viewModel::onStatusFilterCycle,
        onFormatFilterChange = viewModel::onFormatFilterChange,
        onAddClick = onAddClick,
        onEditClick = onEditClick,
        onFavoriteToggle = { viewModel.update(it) },
        onProgressUpdate = { viewModel.update(it) },
        onExportCsv = { onExportCsv(viewModel.buildCsvContent(csvHeader)) },
        onExportDb = onExportDb,
        onImportDb = onImportDb,
        onDiscoverManga = viewModel::loadTopManga,
        onLoadMoreManga = viewModel::loadMoreManga,
        onAddToWishlist = viewModel::addToWishlist,
        onCheckForUpdates = viewModel::checkForUpdates,
        onDismissUpdateCheck = viewModel::dismissUpdateCheck,
    )
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun CollectionContent(
    items: List<CollectionItem>,
    allItems: List<CollectionItem>,
    searchQuery: String,
    activeFilter: CollectionFilter?,
    activeStatusFilters: StatusFilters,
    activeFormat: FormatType?,
    discoverState: DiscoverState,
    updateCheckState: UpdateCheckState,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFilterChange: (CollectionFilter?) -> Unit,
    onStatusFilterCycle: (StatusFilterField) -> Unit,
    onFormatFilterChange: (FormatType?) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (CollectionItem) -> Unit,
    onFavoriteToggle: (CollectionItem) -> Unit,
    onProgressUpdate: (CollectionItem) -> Unit,
    onExportCsv: () -> Unit,
    onExportDb: () -> Unit,
    onImportDb: () -> Unit,
    onDiscoverManga: () -> Unit,
    onLoadMoreManga: () -> Unit,
    onAddToWishlist: (SearchResult) -> Unit,
    onCheckForUpdates: (CollectionItem) -> Unit,
    onDismissUpdateCheck: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val statsScrollState = rememberScrollState()
    var showDiscoverSheet by remember { mutableStateOf(false) }

    // Hide header on scroll-down, reveal on scroll-up — CoordinatorLayout style.
    // NestedScrollConnection receives raw deltas so it is immune to list re-anchoring at the bottom.
    var showHeader by remember { mutableStateOf(true) }
    val headerScrollConnection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (available.y < -2f) {
                        showHeader = false
                    } else if (available.y > 2f) {
                        showHeader = true
                    }
                    return Offset.Zero
                }
            }
        }

    if (showDiscoverSheet) {
        DiscoverMangaBottomSheet(
            state = discoverState,
            onDismiss = { showDiscoverSheet = false },
            onRetry = onDiscoverManga,
            onLoadMore = onLoadMoreManga,
            onAddToWishlist = { result ->
                onAddToWishlist(result)
                showDiscoverSheet = false
            },
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.add_accessibility),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .nestedScroll(headerScrollConnection),
        ) {
            // ── Collapsible header (search + filters) ─────────────────────────
            AnimatedVisibility(
                visible = showHeader,
                enter =
                    expandVertically(animationSpec = tween(800, easing = FastOutSlowInEasing)) +
                        fadeIn(animationSpec = tween(800)),
                exit =
                    shrinkVertically(animationSpec = tween(800, easing = FastOutSlowInEasing)) +
                        fadeOut(animationSpec = tween(800)),
            ) {
                Column {
                    Image(
                        painter = painterResource(Res.drawable.cat),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .height(70.dp)
                                .align(Alignment.Start)
                                .padding(start = 16.dp, top = 12.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    )
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 4.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchChange,
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(stringResource(Res.string.search_placeholder)) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = onClearSearch) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(Res.string.clear_search_accessibility),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onClearSearch() }),
                        )
                        MenuItem(
                            onAddBook = onAddClick,
                            onShowDiscoverSheet = {
                                showDiscoverSheet = true
                                onDiscoverManga()
                            },
                            onExportCsv = onExportCsv,
                            onExportDb = onExportDb,
                            onImportDb = onImportDb,
                        )
                    }
                    FilterRow(activeFilter = activeFilter, onFilterChange = onFilterChange)
                    FormatFilterRow(
                        activeFilter = activeFilter,
                        activeFormat = activeFormat,
                        onFormatFilterChange = onFormatFilterChange,
                    )
                    SubFilterRow(
                        activeStatusFilters = activeStatusFilters,
                        onStatusFilterCycle = onStatusFilterCycle,
                    )
                }
            }

            // ── View tabs (always visible) ────────────────────────────────────
            ViewTabs(
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
            )

            // ── Swipeable pages: list (0) / stats (1) ────────────────────────
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (page) {
                    0 -> {
                        CollectionListPage(
                            items = items,
                            searchQuery = searchQuery,
                            activeFilter = activeFilter,
                            hasActiveStatusFilters = !activeStatusFilters.isDefault,
                            listState = listState,
                            updateCheckState = updateCheckState,
                            onEditClick = onEditClick,
                            onFavoriteToggle = onFavoriteToggle,
                            onProgressUpdate = onProgressUpdate,
                            onCheckForUpdates = onCheckForUpdates,
                            onDismissUpdateCheck = onDismissUpdateCheck,
                        )
                    }

                    else -> {
                        StatisticsScreen(
                            allItems = allItems,
                            filteredItems = items,
                            scrollState = statsScrollState,
                        )
                    }
                }
            }
        }
    }
}

// ── View tabs ─────────────────────────────────────────────────────────────────

@Composable
private fun ViewTabs(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    PrimaryTabRow(selectedTabIndex = selectedIndex) {
        Tab(
            selected = selectedIndex == 0,
            onClick = { onTabSelected(0) },
            text = { Text(stringResource(Res.string.tab_collection)) },
        )
        Tab(
            selected = selectedIndex == 1,
            onClick = { onTabSelected(1) },
            text = { Text(stringResource(Res.string.tab_stats)) },
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

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

/** All filter selected, list with items. */
@Preview
@Composable
private fun PreviewCollectionWithItems() {
    BookMemoTheme {
        CollectionContent(
            items = sampleItems,
            allItems = sampleItems,
            searchQuery = "",
            activeFilter = null,
            activeStatusFilters = StatusFilters(),
            activeFormat = null,
            discoverState = DiscoverState.Idle,
            onSearchChange = {},
            onClearSearch = {},
            onFilterChange = {},
            onStatusFilterCycle = {},
            onFormatFilterChange = {},
            onAddClick = {},
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
            onDiscoverManga = {},
            onLoadMoreManga = {},
            onAddToWishlist = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}

/** Books filter + Favorites YES status filter selected. */
@Preview
@Composable
private fun PreviewCollectionBooksAndFavorites() {
    BookMemoTheme {
        CollectionContent(
            items = sampleItems.filter { it.type == ItemType.LITERATURE && it.favorite },
            allItems = sampleItems,
            searchQuery = "",
            activeFilter = CollectionFilter.LITERATURE,
            activeStatusFilters = StatusFilters(favorites = TriState.YES),
            activeFormat = null,
            discoverState = DiscoverState.Idle,
            onSearchChange = {},
            onClearSearch = {},
            onFilterChange = {},
            onStatusFilterCycle = {},
            onFormatFilterChange = {},
            onAddClick = {},
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
            onDiscoverManga = {},
            onLoadMoreManga = {},
            onAddToWishlist = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}

/** Empty collection — first-launch state. */
@Preview
@Composable
private fun PreviewCollectionEmpty() {
    BookMemoTheme {
        CollectionContent(
            items = emptyList(),
            allItems = emptyList(),
            searchQuery = "",
            activeFilter = null,
            activeStatusFilters = StatusFilters(),
            activeFormat = null,
            discoverState = DiscoverState.Idle,
            onSearchChange = {},
            onClearSearch = {},
            onFilterChange = {},
            onStatusFilterCycle = {},
            onFormatFilterChange = {},
            onAddClick = {},
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
            onDiscoverManga = {},
            onLoadMoreManga = {},
            onAddToWishlist = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}

/** Search returned no results. */
@Preview
@Composable
private fun PreviewCollectionNoResults() {
    BookMemoTheme {
        CollectionContent(
            items = emptyList(),
            allItems = sampleItems,
            searchQuery = "Dune",
            activeFilter = null,
            activeStatusFilters = StatusFilters(),
            activeFormat = null,
            discoverState = DiscoverState.Idle,
            onSearchChange = {},
            onClearSearch = {},
            onFilterChange = {},
            onStatusFilterCycle = {},
            onFormatFilterChange = {},
            onAddClick = {},
            onEditClick = {},
            onFavoriteToggle = {},
            onProgressUpdate = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
            onDiscoverManga = {},
            onLoadMoreManga = {},
            onAddToWishlist = {},
            updateCheckState = UpdateCheckState.Idle,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
        )
    }
}
