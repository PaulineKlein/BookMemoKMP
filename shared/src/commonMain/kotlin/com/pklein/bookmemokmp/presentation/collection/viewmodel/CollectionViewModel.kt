package com.pklein.bookmemokmp.presentation.collection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.FormatType
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.domain.usecase.AddItemUseCase
import com.pklein.bookmemokmp.domain.usecase.BookSearchUseCase
import com.pklein.bookmemokmp.domain.usecase.DeleteItemUseCase
import com.pklein.bookmemokmp.domain.usecase.GetCollectionUseCase
import com.pklein.bookmemokmp.domain.usecase.UpdateItemUseCase
import com.pklein.bookmemokmp.presentation.collection.filter.CollectionFilter
import com.pklein.bookmemokmp.presentation.collection.filter.StatusFilterField
import com.pklein.bookmemokmp.presentation.collection.filter.StatusFilters
import com.pklein.bookmemokmp.presentation.collection.filter.TriState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UpdateCheckState {
    data object Idle : UpdateCheckState

    data object Loading : UpdateCheckState

    data class NewContent(
        val newTotTome: Int?,
        val newTotEpisode: Int?,
        val userTome: Int?,
        val userEpisode: Int?,
    ) : UpdateCheckState

    data object UpToDate : UpdateCheckState

    data object Error : UpdateCheckState
}

sealed interface DiscoverState {
    data object Idle : DiscoverState

    data object Loading : DiscoverState

    data class Success(
        val results: List<SearchResult>,
        val hasNextPage: Boolean = false,
        val isLoadingMore: Boolean = false,
    ) : DiscoverState

    data object Error : DiscoverState

    data object Empty : DiscoverState
}

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModel(
    private val getCollection: GetCollectionUseCase,
    private val bookSearch: BookSearchUseCase,
    private val addItem: AddItemUseCase,
    private val updateItem: UpdateItemUseCase,
    private val deleteItem: DeleteItemUseCase,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow<CollectionFilter?>(null)
    val filter: StateFlow<CollectionFilter?> = _filter.asStateFlow()

    private val _statusFilters = MutableStateFlow(StatusFilters())
    val statusFilters: StateFlow<StatusFilters> = _statusFilters.asStateFlow()

    private val _formatFilter = MutableStateFlow<FormatType?>(null)
    val formatFilter: StateFlow<FormatType?> = _formatFilter.asStateFlow()

    // Full unfiltered list, used for statistics
    val allItems: StateFlow<List<CollectionItem>> =
        getCollection
            .all()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    // Combines type filter + status filters + search into a single reactive stream
    private data class FilterParams(
        val query: String,
        val filter: CollectionFilter?,
        val statusFilters: StatusFilters,
        val formatFilter: FormatType?,
    )

    val displayedItems: StateFlow<List<CollectionItem>> =
        combine(
            _searchQuery,
            _filter,
            _statusFilters,
            _formatFilter,
        ) { query, filter, status, fmt ->
            FilterParams(query, filter, status, fmt)
        }.flatMapLatest { params ->
            val sourceFlow =
                params.filter?.let { getCollection.byType(it.itemType) }
                    ?: getCollection.all()
            sourceFlow.map { items ->
                var result = items
                if (params.query.isNotBlank()) {
                    result =
                        result.filter { it.matchesQuery(params.query) }
                }
                result = result.applyTriState(params.statusFilters.favorites) { it.favorite }
                result = result.applyTriState(params.statusFilters.bought) { it.bought }
                result = result.applyTriState(params.statusFilters.wishlist) { it.wishlist }
                result = result.applyTriState(params.statusFilters.finished) { it.finished }
                result = result.applyTriState(params.statusFilters.loan) { it.isBorrowed }
                result = result.applyTriState(params.statusFilters.isDigital) { it.isDigital }
                params.formatFilter?.let { fmt -> result = result.filter { it.format == fmt } }
                result
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun onFilterChange(filter: CollectionFilter?) {
        _filter.value = filter
        _formatFilter.value = null
    }

    fun onFormatFilterChange(format: FormatType?) {
        _formatFilter.value = format
    }

    fun onStatusFilterCycle(field: StatusFilterField) {
        _statusFilters.update { field.cycle(it) }
    }

    fun add(item: CollectionItem) = viewModelScope.launch { addItem(item) }

    fun update(item: CollectionItem) = viewModelScope.launch { updateItem(item) }

    fun delete(id: Long) = viewModelScope.launch { deleteItem(id) }

    suspend fun getById(id: Long): CollectionItem? = getCollection.getById(id)

    // ── Discover Manga and Author ──────────────────────────────────────────────────────────────

    private val _discoverState = MutableStateFlow<DiscoverState>(DiscoverState.Idle)
    val discoverState: StateFlow<DiscoverState> = _discoverState.asStateFlow()
    private var discoverPage = 1

    fun loadTopManga() {
        if (_discoverState.value is DiscoverState.Loading) return
        discoverPage = 1
        _discoverState.value = DiscoverState.Loading
        viewModelScope.launch {
            _discoverState.value = fetchTopMangaPage(page = 1, accumulated = emptyList())
        }
    }

    fun loadMoreManga() {
        val current = _discoverState.value as? DiscoverState.Success ?: return
        if (!current.hasNextPage || current.isLoadingMore) return
        _discoverState.value = current.copy(isLoadingMore = true)
        viewModelScope.launch {
            _discoverState.value =
                fetchTopMangaPage(
                    page = discoverPage + 1,
                    accumulated = current.results,
                ).let { next ->
                    // If the next page fetch fails, restore the previous results
                    if (next is DiscoverState.Error) {
                        current.copy(isLoadingMore = false)
                    } else {
                        next
                    }
                }
        }
    }

    private suspend fun fetchTopMangaPage(
        page: Int,
        accumulated: List<SearchResult>,
    ): DiscoverState =
        runCatching { bookSearch.fetchTopManga(page) }
            .fold(
                onSuccess = { (searchResults, hasNextPage) ->
                    val filteredResults =
                        filteredResultsFromExistingCollection(
                            results = searchResults,
                            item = ItemType.MANGA,
                        )
                    val all = accumulated + filteredResults
                    if (all.isEmpty()) {
                        DiscoverState.Empty
                    } else {
                        discoverPage = page
                        DiscoverState.Success(all, hasNextPage = hasNextPage)
                    }
                },
                onFailure = { DiscoverState.Error },
            )

    fun loadBooksFromAuthor(
        item: CollectionItem,
        langRestrict: String?,
    ) {
        if (_discoverState.value is DiscoverState.Loading) return
        discoverPage = 1
        _discoverState.value = DiscoverState.Loading
        viewModelScope.launch {
            _discoverState.value = searchAuthor(item, langRestrict)
        }
    }

    private suspend fun searchAuthor(
        item: CollectionItem,
        langRestrict: String?,
    ): DiscoverState =
        runCatching {
            if (item.author.isNullOrBlank()) {
                return DiscoverState.Error
            }
            bookSearch.fetchMoreBooksFromAuthor(
                item.type,
                item.author,
                item.mangaApiAuthorId,
                langRestrict,
            )
        }.fold(
            onSuccess = { searchResults ->
                val filteredResults =
                    filteredResultsFromExistingCollection(
                        results = searchResults,
                        item = item.type,
                    )
                if (filteredResults.isEmpty()) {
                    DiscoverState.Empty
                } else {
                    DiscoverState.Success(
                        results = filteredResults,
                        hasNextPage = false, // TODO on est sure de ca ?
                        isLoadingMore = false,
                    )
                }
            },
            onFailure = { DiscoverState.Error },
        )

    fun filteredResultsFromExistingCollection(
        results: List<SearchResult>,
        item: ItemType,
    ): List<SearchResult> {
        val collectionKeys =
            allItems.value
                .filter { it.type == item }
                .map { it.title.trim().lowercase().normalizeSeriesTitle() }
                .toSet()

        return results.filter { result ->
            result.title.trim().lowercase().normalizeSeriesTitle() !in collectionKeys
        }
    }

    fun addToWishlist(
        result: SearchResult,
        type: ItemType,
    ) = viewModelScope.launch {
        addItem(
            CollectionItem(
                type = type,
                title = result.title,
                author = result.author,
                year = result.year,
                description = result.description,
                imageUrl = result.imageUrl,
                wishlist = true,
                mangaApiId = result.mangaApiId,
                mangaApiAuthorId = result.mangaApiAuthorId,
                mangaApiType = result.mangaApiType,
                totTome = result.totTome,
                totChapter = result.totChapter,
                totEpisode = result.totEpisode,
            ),
        )
    }

    // ── Update check ─────────────────────────────────────────────────────────

    private val _updateCheckState = MutableStateFlow<UpdateCheckState>(UpdateCheckState.Idle)
    val updateCheckState: StateFlow<UpdateCheckState> = _updateCheckState.asStateFlow()

    fun checkForUpdates(item: CollectionItem) {
        val jikanId = item.mangaApiId ?: return
        val jikanType = item.mangaApiType ?: return
        if (_updateCheckState.value is UpdateCheckState.Loading) return
        _updateCheckState.value = UpdateCheckState.Loading
        viewModelScope.launch {
            _updateCheckState.value =
                runCatching {
                    bookSearch.checkForUpdates(jikanId, jikanType)
                }.fold(
                    onSuccess = { result ->
                        val freshTome = result.totTome
                        val freshEpisode = result.totEpisode
                        val oldTome = item.totTome
                        val oldEpisode = item.totEpisode
                        val hasNewTomes =
                            freshTome != null && (oldTome == null || freshTome > oldTome)
                        val hasNewEpisodes =
                            freshEpisode != null && (oldEpisode == null || freshEpisode > oldEpisode)
                        // Backfill author id for existing items saved before this field existed
                        if (item.mangaApiAuthorId == null && result.mangaApiAuthorId != null) {
                            updateItem(item.copy(mangaApiAuthorId = result.mangaApiAuthorId))
                        }
                        if (hasNewTomes || hasNewEpisodes) {
                            getCollection.updateTotals(
                                item.id,
                                result.totTome,
                                result.totChapter,
                                result.totEpisode,
                            )
                            UpdateCheckState.NewContent(
                                newTotTome = result.totTome,
                                newTotEpisode = result.totEpisode,
                                userTome = item.tome,
                                userEpisode = item.episode,
                            )
                        } else {
                            UpdateCheckState.UpToDate
                        }
                    },
                    onFailure = { UpdateCheckState.Error },
                )
        }
    }

    fun dismissUpdateCheck() {
        _updateCheckState.value = UpdateCheckState.Idle
    }

    // ── Builds a CSV string from the full unfiltered collection. ──────────────────────────────
    fun buildCsvContent(headline: String): String =
        buildString {
            appendLine(headline)
            allItems.value.forEach { item ->
                append(item.type.name.csvEscape())
                append(",")
                append((item.format?.name ?: "").csvEscape())
                append(",")
                append(item.title.csvEscape())
                append(",")
                append((item.author ?: "").csvEscape())
                append(",")
                append((item.illustrator ?: "").csvEscape())
                append(",")
                append(item.year?.toString() ?: "")
                append(",")
                append(item.bought)
                append(",")
                append(item.wishlist)
                append(",")
                append(item.finished)
                append(",")
                append(item.tome?.toString() ?: "")
                append(",")
                append(item.totTome?.toString() ?: "")
                append(",")
                append(item.chapter?.toString() ?: "")
                append(",")
                append(item.totChapter?.toString() ?: "")
                append(",")
                append(item.episode?.toString() ?: "")
                append(",")
                append(item.totEpisode?.toString() ?: "")
                append(",")
                append(item.season?.toString() ?: "")
                append(",")
                append((item.description ?: "").csvEscape())
                append(",")
                append(item.favorite)
                append(",")
                append((item.imageUrl ?: "").csvEscape())
                append(",")
                append(item.isDigital)
                append(",")
                append(item.isBorrowed)
                append(",")
                append(item.borrowedSince?.toString() ?: "")
                append(",")
                append((item.borrowedBy ?: "").csvEscape())
                append(",")
                append(item.checkedTomes.joinToString(";").csvEscape())
                append(",")
                appendLine((item.notes ?: "").csvEscape())
            }
        }

    private fun String.csvEscape(): String = "\"${replace("\"", "\"\"")}\""

    private fun List<CollectionItem>.applyTriState(
        state: TriState,
        predicate: (CollectionItem) -> Boolean,
    ): List<CollectionItem> =
        when (state) {
            TriState.ALL -> this
            TriState.YES -> filter(predicate)
            TriState.NO -> filter { !predicate(it) }
        }

    /** In-memory text search used when a type or status filter is active. */
    private fun CollectionItem.matchesQuery(query: String): Boolean {
        val q = query.trim().lowercase()
        return title.lowercase().contains(q) ||
            author?.lowercase()?.contains(q) == true ||
            description?.lowercase()?.contains(q) == true
    }
}

private val seriesTitleNormRegex = Regex(
    """[,\s\-–]+(?:vol(?:ume)?\.?|tome|t\.|no\.?|n°|#|book|bd\.?|volume)\s*\d+[\d/]*.*$""" +
        """|[(\s\-–]+\d+(?:/\d+)?[)\s]*$""",
    RegexOption.IGNORE_CASE,
)

private fun String.normalizeSeriesTitle(): String = seriesTitleNormRegex.replace(this, "").trim()
