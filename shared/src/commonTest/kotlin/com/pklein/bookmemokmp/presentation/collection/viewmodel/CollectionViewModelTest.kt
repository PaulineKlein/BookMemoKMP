package com.pklein.bookmemokmp.presentation.collection.viewmodel

import app.cash.sqldelight.db.QueryResult
import com.pklein.bookmemokmp.data.remote.JikanUpdateResult
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.JikanType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.domain.repository.IBookSearchRepository
import com.pklein.bookmemokmp.domain.repository.ICollectionRepository
import com.pklein.bookmemokmp.domain.usecase.AddItemUseCase
import com.pklein.bookmemokmp.domain.usecase.BookSearchUseCase
import com.pklein.bookmemokmp.domain.usecase.DeleteItemUseCase
import com.pklein.bookmemokmp.domain.usecase.GetCollectionUseCase
import com.pklein.bookmemokmp.domain.usecase.UpdateItemUseCase
import com.pklein.bookmemokmp.presentation.collection.filter.CollectionFilter
import com.pklein.bookmemokmp.presentation.collection.filter.StatusFilterField
import com.pklein.bookmemokmp.presentation.collection.filter.TriState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

// ── Sample data ────────────────────────────────────────────────────────────────

private val manga1 =
    CollectionItem(
        id = 1,
        type = ItemType.MANGA,
        title = "One Piece",
        favorite = true,
        bought = true,
    )
private val manga2 =
    CollectionItem(id = 2, type = ItemType.MANGA, title = "Naruto", wishlist = true)
private val book1 =
    CollectionItem(id = 3, type = ItemType.LITERATURE, title = "Dune", finished = true)
private val allItems = listOf(manga1, manga2, book1)

// ── Fakes ──────────────────────────────────────────────────────────────────────

private class FakeICollectionRepository : ICollectionRepository {
    private val itemsList = MutableStateFlow(allItems)

    val addedItems = mutableListOf<CollectionItem>()

    fun setItems(items: List<CollectionItem>) {
        itemsList.value = items
    }

    override fun getAll(): Flow<List<CollectionItem>> = itemsList

    override fun getByType(type: ItemType): Flow<List<CollectionItem>> = MutableStateFlow(itemsList.value.filter { it.type == type })

    override fun getFavorites(): Flow<List<CollectionItem>> = flowOf(itemsList.value.filter { it.favorite })

    override suspend fun getById(id: Long): CollectionItem? = itemsList.value.find { it.id == id }

    override suspend fun add(item: CollectionItem): Long {
        addedItems += item
        return 99L
    }

    override suspend fun update(item: CollectionItem) = QueryResult.Value(1L)

    override suspend fun updateLoan(
        id: Long,
        isBorrowed: Boolean,
        borrowedSince: Long?,
        borrowedBy: String?,
    ) = QueryResult.Value(1L)

    override suspend fun updateTotals(
        id: Long,
        totTome: Int?,
        totChapter: Int?,
        totEpisode: Int?,
    ) = QueryResult.Value(1L)

    override suspend fun delete(id: Long) = QueryResult.Value(1L)

    override suspend fun existsByTitleAndType(
        title: String,
        type: ItemType,
        excludeId: Long,
    ) = false
}

private class FakeBookSearchRepository(
    private val topMangaResult: Pair<List<SearchResult>, Boolean> = emptyList<SearchResult>() to false,
    private val mangaUpdate: JikanUpdateResult = JikanUpdateResult(null, null, null),
    private val shouldFail: Boolean = false,
) : IBookSearchRepository {
    override suspend fun fetchTopManga(page: Int): Pair<List<SearchResult>, Boolean> {
        if (shouldFail) throw RuntimeException("Network error")
        return topMangaResult
    }

    override suspend fun fetchMangaUpdate(malId: Long): JikanUpdateResult {
        if (shouldFail) throw RuntimeException("Network error")
        return mangaUpdate
    }

    override suspend fun fetchAnimeUpdate(malId: Long): JikanUpdateResult {
        if (shouldFail) throw RuntimeException("Network error")
        return mangaUpdate
    }

    override suspend fun search(
        query: String,
        type: ItemType,
        langRestrict: String?,
    ) = emptyList<SearchResult>()

    override suspend fun searchByIsbn(isbn: String) = emptyList<SearchResult>()
}

// ── Helpers ────────────────────────────────────────────────────────────────────

/**
 * Subscribes to the flows that use WhileSubscribed so they start collecting.
 * Must be called inside runTest { } using backgroundScope.
 */
@OptIn(ExperimentalCoroutinesApi::class)
private fun kotlinx.coroutines.test.TestScope.activateFlows(vm: CollectionViewModel) {
    backgroundScope.launch { vm.allItems.collect() }
    backgroundScope.launch { vm.displayedItems.collect() }
}

// ── Builder ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
private fun buildViewModel(
    collectionRepo: FakeICollectionRepository = FakeICollectionRepository(),
    searchRepo: IBookSearchRepository = FakeBookSearchRepository(),
): CollectionViewModel {
    val getCollection = GetCollectionUseCase(collectionRepo)
    val bookSearch = BookSearchUseCase(searchRepo)
    val addItem = AddItemUseCase(collectionRepo)
    val updateItem = UpdateItemUseCase(collectionRepo)
    val deleteItem = DeleteItemUseCase(collectionRepo)
    return CollectionViewModel(getCollection, bookSearch, addItem, updateItem, deleteItem)
}

// ── Tests ──────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Search ─────────────────────────────────────────────────────────────────

    @Test
    fun `onSearchQueryChange updates searchQuery`() =
        runTest {
            val vm = buildViewModel()
            vm.onSearchQueryChange("one piece")
            assertEquals("one piece", vm.searchQuery.value)
        }

    @Test
    fun `clearSearch resets searchQuery to empty`() =
        runTest {
            val vm = buildViewModel()
            vm.onSearchQueryChange("naruto")
            vm.clearSearch()
            assertEquals("", vm.searchQuery.value)
        }

    @Test
    fun `displayedItems filters by search query`() =
        runTest {
            val vm = buildViewModel()
            activateFlows(vm)
            advanceUntilIdle()

            vm.onSearchQueryChange("dune")
            advanceUntilIdle()

            val items = vm.displayedItems.value
            assertEquals(1, items.size)
            assertEquals("Dune", items.first().title)
        }

    @Test
    fun `displayedItems search is case-insensitive`() =
        runTest {
            val vm = buildViewModel()
            activateFlows(vm)
            advanceUntilIdle()

            vm.onSearchQueryChange("ONE PIECE")
            advanceUntilIdle()

            assertEquals(1, vm.displayedItems.value.size)
        }

    // ── Type filter ────────────────────────────────────────────────────────────

    @Test
    fun `onFilterChange to MANGA shows only manga items`() =
        runTest {
            val vm = buildViewModel()
            activateFlows(vm)
            advanceUntilIdle()

            vm.onFilterChange(CollectionFilter.MANGA)
            advanceUntilIdle()

            val items = vm.displayedItems.value
            assertTrue(items.isNotEmpty())
            assertTrue(items.all { it.type == ItemType.MANGA })
        }

    @Test
    fun `onFilterChange to null shows all items`() =
        runTest {
            val vm = buildViewModel()
            activateFlows(vm)
            advanceUntilIdle()

            vm.onFilterChange(CollectionFilter.MANGA)
            advanceUntilIdle()
            vm.onFilterChange(null)
            advanceUntilIdle()

            assertEquals(allItems.size, vm.displayedItems.value.size)
        }

    @Test
    fun `onFilterChange resets formatFilter to null`() =
        runTest {
            val vm = buildViewModel()
            vm.onFilterChange(CollectionFilter.MANGA)
            assertEquals(null, vm.formatFilter.value)
        }

    // ── Status filters ─────────────────────────────────────────────────────────

    @Test
    fun `status filter cycles ALL to YES to NO and back`() =
        runTest {
            val vm = buildViewModel()

            assertEquals(TriState.ALL, vm.statusFilters.value.favorites)
            vm.onStatusFilterCycle(StatusFilterField.FAVORITES)
            assertEquals(TriState.YES, vm.statusFilters.value.favorites)
            vm.onStatusFilterCycle(StatusFilterField.FAVORITES)
            assertEquals(TriState.NO, vm.statusFilters.value.favorites)
            vm.onStatusFilterCycle(StatusFilterField.FAVORITES)
            assertEquals(TriState.ALL, vm.statusFilters.value.favorites)
        }

    @Test
    fun `displayedItems filters by favorites YES`() =
        runTest {
            val vm = buildViewModel()
            activateFlows(vm)
            advanceUntilIdle()

            vm.onStatusFilterCycle(StatusFilterField.FAVORITES) // ALL → YES
            advanceUntilIdle()

            val items = vm.displayedItems.value
            assertTrue(items.isNotEmpty())
            assertTrue(items.all { it.favorite })
        }

    @Test
    fun `displayedItems filters by wishlist YES`() =
        runTest {
            val vm = buildViewModel()
            activateFlows(vm)
            advanceUntilIdle()

            vm.onStatusFilterCycle(StatusFilterField.WISHLIST) // ALL → YES
            advanceUntilIdle()

            assertTrue(vm.displayedItems.value.all { it.wishlist })
        }

    @Test
    fun `displayedItems filters bought NO excludes bought items`() =
        runTest {
            val vm = buildViewModel()
            activateFlows(vm)
            advanceUntilIdle()

            vm.onStatusFilterCycle(StatusFilterField.BOUGHT) // ALL → YES
            vm.onStatusFilterCycle(StatusFilterField.BOUGHT) // YES → NO
            advanceUntilIdle()

            assertTrue(vm.displayedItems.value.all { !it.bought })
        }

    // ── CSV ────────────────────────────────────────────────────────────────────

    @Test
    fun `buildCsvContent has headline plus one line per item`() =
        runTest {
            val vm = buildViewModel()
            activateFlows(vm)
            advanceUntilIdle()

            val csv = vm.buildCsvContent("Type,Title")
            val lines = csv.trim().lines()
            assertEquals(allItems.size + 1, lines.size)
            assertEquals("Type,Title", lines.first())
        }

    @Test
    fun `buildCsvContent escapes double quotes in title`() =
        runTest {
            val repo = FakeICollectionRepository()
            repo.setItems(
                listOf(
                    CollectionItem(
                        id = 1,
                        type = ItemType.LITERATURE,
                        title = "He said \"hello\"",
                    ),
                ),
            )
            val vm = buildViewModel(collectionRepo = repo)
            activateFlows(vm)
            advanceUntilIdle()

            val csv = vm.buildCsvContent("header")
            assertTrue(csv.contains("\"He said \"\"hello\"\"\""))
        }

    // ── Discover ───────────────────────────────────────────────────────────────

    @Test
    fun `loadTopManga transitions to Success with results`() =
        runTest {
            val results =
                listOf(
                    SearchResult(
                        title = "Berserk",
                        author = null,
                        year = null,
                        description = null,
                    ),
                )
            val vm =
                buildViewModel(searchRepo = FakeBookSearchRepository(topMangaResult = results to false))

            vm.loadTopManga()
            advanceUntilIdle()

            val state = vm.discoverState.value
            assertIs<DiscoverState.Success>(state)
            assertEquals("Berserk", state.results.first().title)
        }

    @Test
    fun `loadTopManga sets Error on network failure`() =
        runTest {
            val vm = buildViewModel(searchRepo = FakeBookSearchRepository(shouldFail = true))

            vm.loadTopManga()
            advanceUntilIdle()

            assertIs<DiscoverState.Error>(vm.discoverState.value)
        }

    @Test
    fun `loadTopManga filters out manga already in collection`() =
        runTest {
            val results =
                listOf(
                    SearchResult(
                        title = "One Piece",
                        author = null,
                        year = null,
                        description = null,
                    ),
                    SearchResult(title = "Berserk", author = null, year = null, description = null),
                )
            val vm =
                buildViewModel(searchRepo = FakeBookSearchRepository(topMangaResult = results to false))
            activateFlows(vm)
            advanceUntilIdle()

            vm.loadTopManga()
            advanceUntilIdle()

            val state = vm.discoverState.value as DiscoverState.Success
            assertEquals(1, state.results.size)
            assertEquals("Berserk", state.results.first().title)
        }

    @Test
    fun `loadTopManga is ignored while already loading`() =
        runTest {
            val vm = buildViewModel(searchRepo = FakeBookSearchRepository(shouldFail = true))

            vm.loadTopManga()
            vm.loadTopManga() // ignored — first call is still Loading
            advanceUntilIdle()

            assertIs<DiscoverState.Error>(vm.discoverState.value)
        }

    // ── Update check ───────────────────────────────────────────────────────────

    @Test
    fun `checkForUpdates returns UpToDate when totTome unchanged`() =
        runTest {
            val item = manga1.copy(jikanId = 42L, jikanType = JikanType.MANGA, totTome = 107)
            val vm =
                buildViewModel(
                    searchRepo =
                        FakeBookSearchRepository(
                            mangaUpdate =
                                JikanUpdateResult(
                                    totTome = 107,
                                    totChapter = null,
                                    totEpisode = null,
                                ),
                        ),
                )

            vm.checkForUpdates(item)
            advanceUntilIdle()

            assertIs<UpdateCheckState.UpToDate>(vm.updateCheckState.value)
        }

    @Test
    fun `checkForUpdates returns NewContent when fresh totTome is higher`() =
        runTest {
            val item = manga1.copy(jikanId = 42L, jikanType = JikanType.MANGA, totTome = 100)
            val vm =
                buildViewModel(
                    searchRepo =
                        FakeBookSearchRepository(
                            mangaUpdate =
                                JikanUpdateResult(
                                    totTome = 110,
                                    totChapter = null,
                                    totEpisode = null,
                                ),
                        ),
                )

            vm.checkForUpdates(item)
            advanceUntilIdle()

            val state = vm.updateCheckState.value
            assertIs<UpdateCheckState.NewContent>(state)
            assertEquals(110, state.newTotTome)
        }

    @Test
    fun `checkForUpdates returns Error on network failure`() =
        runTest {
            val item = manga1.copy(jikanId = 42L, jikanType = JikanType.MANGA)
            val vm = buildViewModel(searchRepo = FakeBookSearchRepository(shouldFail = true))

            vm.checkForUpdates(item)
            advanceUntilIdle()

            assertIs<UpdateCheckState.Error>(vm.updateCheckState.value)
        }

    @Test
    fun `checkForUpdates is no-op when jikanId is null`() =
        runTest {
            val item = manga1.copy(jikanId = null)
            val vm = buildViewModel()

            vm.checkForUpdates(item)
            advanceUntilIdle()

            assertIs<UpdateCheckState.Idle>(vm.updateCheckState.value)
        }

    @Test
    fun `dismissUpdateCheck resets state to Idle`() =
        runTest {
            val item = manga1.copy(jikanId = 42L, jikanType = JikanType.MANGA, totTome = 100)
            val vm =
                buildViewModel(
                    searchRepo =
                        FakeBookSearchRepository(
                            mangaUpdate =
                                JikanUpdateResult(
                                    totTome = 110,
                                    totChapter = null,
                                    totEpisode = null,
                                ),
                        ),
                )

            vm.checkForUpdates(item)
            advanceUntilIdle()
            vm.dismissUpdateCheck()

            assertIs<UpdateCheckState.Idle>(vm.updateCheckState.value)
        }

    // ── addToWishlist ──────────────────────────────────────────────────────────

    @Test
    fun `addToWishlist saves item with wishlist=true and correct type`() =
        runTest {
            val repo = FakeICollectionRepository()
            val vm = buildViewModel(collectionRepo = repo)

            val result =
                SearchResult(
                    title = "Vagabond",
                    author = "Inoue",
                    year = 1998,
                    description = null,
                    jikanId = 7L,
                    jikanType = JikanType.MANGA,
                )
            vm.addToWishlist(result)
            advanceUntilIdle()

            assertEquals(1, repo.addedItems.size)
            val added = repo.addedItems.first()
            assertEquals("Vagabond", added.title)
            assertTrue(added.wishlist)
            assertEquals(ItemType.MANGA, added.type)
            assertEquals(7L, added.jikanId)
        }
}
