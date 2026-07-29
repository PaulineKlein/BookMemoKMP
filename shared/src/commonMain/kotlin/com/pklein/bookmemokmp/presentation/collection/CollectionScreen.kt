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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.add_accessibility
import bookmemokmp.shared.generated.resources.cat
import bookmemokmp.shared.generated.resources.clear_search_accessibility
import bookmemokmp.shared.generated.resources.search_placeholder
import bookmemokmp.shared.generated.resources.tab_collection
import bookmemokmp.shared.generated.resources.tab_stats
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.FormatType
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.presentation.collection.collectionList.CollectionListPage
import com.pklein.bookmemokmp.presentation.collection.discover.DiscoverBookBottomSheet
import com.pklein.bookmemokmp.presentation.collection.discover.DiscoverTypeState
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
    onSettingsClick: () -> Unit = {},
) {
    val items by viewModel.displayedItems.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val statusFilters by viewModel.statusFilters.collectAsState()
    val formatFilter by viewModel.formatFilter.collectAsState()
    val discoverState by viewModel.discoverState.collectAsState()
    val updateCheckState by viewModel.updateCheckState.collectAsState()
    val saveEnglishDescription by viewModel.saveEnglishDescription.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current

    CollectionContent(
        items = items,
        allItems = allItems,
        searchQuery = searchQuery,
        activeFilter = filter,
        activeStatusFilters = statusFilters,
        activeFormat = formatFilter,
        discoverState = discoverState,
        updateCheckState = updateCheckState,
        saveEnglishDescription = saveEnglishDescription,
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
        onSettingsClick = onSettingsClick,
        onDiscoverManga = viewModel::loadTopManga,
        onDiscoverOneShots = viewModel::loadTopOneShots,
        onDiscoverNovels = viewModel::loadTopNovels,
        onDiscoverAnime = viewModel::loadTopAnime,
        onLoadMoreRankingPage = viewModel::loadMoreRankingPage,
        onAddToWishlist = viewModel::addToWishlist,
        onCheckForUpdates = viewModel::checkForUpdates,
        onDismissUpdateCheck = viewModel::dismissUpdateCheck,
        onSearchAuthor = viewModel::loadBooksFromAuthor,
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
    saveEnglishDescription: Boolean,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFilterChange: (CollectionFilter?) -> Unit,
    onStatusFilterCycle: (StatusFilterField) -> Unit,
    onFormatFilterChange: (FormatType?) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (CollectionItem) -> Unit,
    onFavoriteToggle: (CollectionItem) -> Unit,
    onProgressUpdate: (CollectionItem) -> Unit,
    onSettingsClick: () -> Unit,
    onDiscoverManga: () -> Unit,
    onDiscoverOneShots: () -> Unit,
    onDiscoverNovels: () -> Unit,
    onDiscoverAnime: () -> Unit,
    onLoadMoreRankingPage: () -> Unit,
    onAddToWishlist: (SearchResult, ItemType, FormatType?) -> Unit,
    onCheckForUpdates: (CollectionItem) -> Unit,
    onSearchAuthor: (CollectionItem, String?) -> Unit,
    onDismissUpdateCheck: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val statsScrollState = rememberScrollState()
    var discoverTypeState by remember { mutableStateOf<DiscoverTypeState>(DiscoverTypeState.Dismissed) }

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

    if (discoverTypeState != DiscoverTypeState.Dismissed) {
        val authorItem = (discoverTypeState as? DiscoverTypeState.Author)?.item
        DiscoverBookBottomSheet(
            state = discoverState,
            type = discoverTypeState,
            saveEnglishDescription = saveEnglishDescription,
            onDismiss = { discoverTypeState = DiscoverTypeState.Dismissed },
            onRetry = {
                when (discoverTypeState) {
                    is DiscoverTypeState.TopManga -> {
                        onDiscoverManga()
                    }

                    is DiscoverTypeState.TopOneShots -> {
                        onDiscoverOneShots()
                    }

                    is DiscoverTypeState.TopNovels -> {
                        onDiscoverNovels()
                    }

                    is DiscoverTypeState.TopAnime -> {
                        onDiscoverAnime()
                    }

                    is DiscoverTypeState.Author -> {
                        authorItem?.let {
                            onSearchAuthor(it, if (Locale.current.language == "fr") "fr" else "en")
                        }
                    }

                    else -> {}
                }
            },
            onLoadMore = onLoadMoreRankingPage,
            onAddToWishlistTopRanking = { result, format ->
                onAddToWishlist(result, ItemType.MANGA, format)
            },
            onAddToWishlistAuthor = { result ->
                onAddToWishlist(result, authorItem?.type ?: ItemType.LITERATURE, null)
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
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.cat),
                            contentDescription = null,
                            modifier = Modifier.height(70.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
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
                            onShowDiscoverSheetManga = {
                                discoverTypeState = DiscoverTypeState.TopManga
                                onDiscoverManga()
                            },
                            onShowDiscoverSheetOneShots = {
                                discoverTypeState = DiscoverTypeState.TopOneShots
                                onDiscoverOneShots()
                            },
                            onShowDiscoverSheetNovels = {
                                discoverTypeState = DiscoverTypeState.TopNovels
                                onDiscoverNovels()
                            },
                            onShowDiscoverSheetAnime = {
                                discoverTypeState = DiscoverTypeState.TopAnime
                                onDiscoverAnime()
                            },
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
                            onSearchAuthor = { item ->
                                discoverTypeState = DiscoverTypeState.Author(item)
                                onSearchAuthor(
                                    item,
                                    if (Locale.current.language == "fr") "fr" else "en",
                                )
                            },
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
            onSettingsClick = {},
            onDiscoverManga = {},
            onDiscoverOneShots = {},
            onDiscoverNovels = {},
            onDiscoverAnime = {},
            onLoadMoreRankingPage = {},
            onAddToWishlist = { _, _, _ -> },
            updateCheckState = UpdateCheckState.Idle,
            saveEnglishDescription = true,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = { _, _ -> },
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
            onSettingsClick = {},
            onDiscoverManga = {},
            onDiscoverOneShots = {},
            onDiscoverNovels = {},
            onDiscoverAnime = {},
            onLoadMoreRankingPage = {},
            onAddToWishlist = { _, _, _ -> },
            updateCheckState = UpdateCheckState.Idle,
            saveEnglishDescription = true,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = { _, _ -> },
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
            onSettingsClick = {},
            onDiscoverManga = {},
            onDiscoverOneShots = {},
            onDiscoverNovels = {},
            onDiscoverAnime = {},
            onLoadMoreRankingPage = {},
            onAddToWishlist = { _, _, _ -> },
            updateCheckState = UpdateCheckState.Idle,
            saveEnglishDescription = true,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = { _, _ -> },
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
            onSettingsClick = {},
            onDiscoverManga = {},
            onDiscoverOneShots = {},
            onDiscoverNovels = {},
            onDiscoverAnime = {},
            onLoadMoreRankingPage = {},
            onAddToWishlist = { _, _, _ -> },
            updateCheckState = UpdateCheckState.Idle,
            saveEnglishDescription = true,
            onCheckForUpdates = {},
            onDismissUpdateCheck = {},
            onSearchAuthor = { _, _ -> },
        )
    }
}
