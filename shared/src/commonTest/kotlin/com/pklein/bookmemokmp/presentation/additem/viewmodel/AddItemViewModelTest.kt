package com.pklein.bookmemokmp.presentation.additem.viewmodel

import app.cash.sqldelight.db.QueryResult
import com.pklein.bookmemokmp.FakeSettings
import com.pklein.bookmemokmp.data.UserPreferencesRepository
import com.pklein.bookmemokmp.data.remote.UpdateResult
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.domain.repository.IBookSearchRepository
import com.pklein.bookmemokmp.domain.repository.ICollectionRepository
import com.pklein.bookmemokmp.domain.usecase.BookSearchUseCase
import com.pklein.bookmemokmp.domain.usecase.GetCollectionUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

// ── Sample data ────────────────────────────────────────────────────────────────

private val onePiece =
    SearchResult(
        title = "One Piece",
        author = "Eiichiro Oda",
        year = 1997,
        description = "Pirates",
    )
private val dune =
    SearchResult(
        title = "Dune",
        author = "Frank Herbert",
        year = 1965,
        description = "Sand",
    )

// ── Fakes ──────────────────────────────────────────────────────────────────────

private class FakeBookSearchRepository : IBookSearchRepository {
    var searchResults: List<SearchResult> = emptyList()
    var isbnResults: List<SearchResult> = emptyList()
    var shouldFail = false

    /** When set, search calls suspend until this is completed. */
    var gate: CompletableDeferred<Unit>? = null

    var searchCalls = 0
    var isbnCalls = 0
    var lastQuery: String? = null
    var lastType: ItemType? = null
    var lastLang: String? = null
    var lastIsbn: String? = null

    override suspend fun search(
        query: String,
        type: ItemType,
        langRestrict: String?,
    ): List<SearchResult> {
        searchCalls++
        lastQuery = query
        lastType = type
        lastLang = langRestrict
        gate?.await()
        if (shouldFail) throw RuntimeException("Network error")
        return searchResults
    }

    override suspend fun searchByIsbn(isbn: String): List<SearchResult> {
        isbnCalls++
        lastIsbn = isbn
        gate?.await()
        if (shouldFail) throw RuntimeException("Network error")
        return isbnResults
    }

    override suspend fun fetchTopManga(
        page: Int,
        rankingType: String,
    ): Pair<List<SearchResult>, Boolean> = emptyList<SearchResult>() to false

    override suspend fun fetchTopAnime(page: Int): Pair<List<SearchResult>, Boolean> = emptyList<SearchResult>() to false

    override suspend fun fetchMangaUpdate(malId: Long) = UpdateResult(null, null, null)

    override suspend fun fetchAnimeUpdate(malId: Long) = UpdateResult(null, null, null)

    override suspend fun fetchMoreBooksFromAuthor(
        type: ItemType,
        author: String,
        authorId: Long?,
        langRestrict: String?,
    ) = emptyList<SearchResult>()
}

private class FakeCollectionRepository : ICollectionRepository {
    var exists = false
    var lastTitle: String? = null
    var lastType: ItemType? = null
    var lastExcludeId: Long? = null

    override suspend fun existsByTitleAndType(
        title: String,
        type: ItemType,
        excludeId: Long,
    ): Boolean {
        lastTitle = title
        lastType = type
        lastExcludeId = excludeId
        return exists
    }

    override fun getAll(): Flow<List<CollectionItem>> = flowOf(emptyList())

    override fun getByType(type: ItemType): Flow<List<CollectionItem>> = flowOf(emptyList())

    override fun getFavorites(): Flow<List<CollectionItem>> = flowOf(emptyList())

    override suspend fun getById(id: Long): CollectionItem? = null

    override suspend fun add(item: CollectionItem): Long = 1L

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
}

// ── Tests ──────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class AddItemViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var searchRepo: FakeBookSearchRepository
    private lateinit var collectionRepo: FakeCollectionRepository
    private lateinit var settings: FakeSettings
    private lateinit var userPrefs: UserPreferencesRepository
    private lateinit var viewModel: AddItemViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        searchRepo = FakeBookSearchRepository()
        collectionRepo = FakeCollectionRepository()
        settings = FakeSettings()
        userPrefs = UserPreferencesRepository(settings)
        viewModel =
            AddItemViewModel(
                BookSearchUseCase(searchRepo),
                userPrefs,
                GetCollectionUseCase(collectionRepo),
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial state ──────────────────────────────────────────────────────────

    @Test
    fun `initial search state is Idle`() {
        assertEquals(SearchState.Idle, viewModel.searchState.value)
    }

    // ── search ─────────────────────────────────────────────────────────────────

    @Test
    fun `search sets Loading immediately`() =
        runTest {
            viewModel.search("one piece", ItemType.MANGA)

            assertEquals(SearchState.Loading, viewModel.searchState.value)
        }

    @Test
    fun `search emits Success with results`() =
        runTest {
            searchRepo.searchResults = listOf(onePiece, dune)

            viewModel.search("one", ItemType.MANGA)
            advanceUntilIdle()

            val state = assertIs<SearchState.Success>(viewModel.searchState.value)
            assertEquals(listOf(onePiece, dune), state.results)
        }

    @Test
    fun `search emits Empty when no results`() =
        runTest {
            searchRepo.searchResults = emptyList()

            viewModel.search("zzz", ItemType.LITERATURE)
            advanceUntilIdle()

            assertEquals(SearchState.Empty, viewModel.searchState.value)
        }

    @Test
    fun `search emits Error when use case throws`() =
        runTest {
            searchRepo.shouldFail = true

            viewModel.search("one", ItemType.MANGA)
            advanceUntilIdle()

            assertEquals(SearchState.Error, viewModel.searchState.value)
        }

    @Test
    fun `search forwards query, type and language to repository`() =
        runTest {
            viewModel.search("dune", ItemType.LITERATURE, "fr")
            advanceUntilIdle()

            assertEquals("dune", searchRepo.lastQuery)
            assertEquals(ItemType.LITERATURE, searchRepo.lastType)
            assertEquals("fr", searchRepo.lastLang)
        }

    @Test
    fun `search defaults language restriction to null`() =
        runTest {
            viewModel.search("dune", ItemType.LITERATURE)
            advanceUntilIdle()

            assertEquals(null, searchRepo.lastLang)
        }

    @Test
    fun `search is ignored while a search is already loading`() =
        runTest {
            val gate = CompletableDeferred<Unit>()
            searchRepo.gate = gate
            searchRepo.searchResults = listOf(onePiece)

            viewModel.search("first", ItemType.MANGA)
            advanceUntilIdle()
            assertEquals(SearchState.Loading, viewModel.searchState.value)

            viewModel.search("second", ItemType.MANGA)
            advanceUntilIdle()

            assertEquals(1, searchRepo.searchCalls)
            assertEquals("first", searchRepo.lastQuery)

            gate.complete(Unit)
            advanceUntilIdle()

            assertIs<SearchState.Success>(viewModel.searchState.value)
        }

    @Test
    fun `search can be retried after an error`() =
        runTest {
            searchRepo.shouldFail = true
            viewModel.search("one", ItemType.MANGA)
            advanceUntilIdle()
            assertEquals(SearchState.Error, viewModel.searchState.value)

            searchRepo.shouldFail = false
            searchRepo.searchResults = listOf(onePiece)
            viewModel.search("one", ItemType.MANGA)
            advanceUntilIdle()

            assertIs<SearchState.Success>(viewModel.searchState.value)
            assertEquals(2, searchRepo.searchCalls)
        }

    // ── searchByIsbn ───────────────────────────────────────────────────────────

    @Test
    fun `searchByIsbn emits Error for null isbn without calling repository`() =
        runTest {
            viewModel.searchByIsbn(null)
            advanceUntilIdle()

            assertEquals(SearchState.Error, viewModel.searchState.value)
            assertEquals(0, searchRepo.isbnCalls)
        }

    @Test
    fun `searchByIsbn emits Error for blank isbn without calling repository`() =
        runTest {
            viewModel.searchByIsbn("   ")
            advanceUntilIdle()

            assertEquals(SearchState.Error, viewModel.searchState.value)
            assertEquals(0, searchRepo.isbnCalls)
        }

    @Test
    fun `searchByIsbn emits NotFoundException when flag is set`() =
        runTest {
            viewModel.searchByIsbn("9782070360024", isNotFoundException = true)
            advanceUntilIdle()

            assertEquals(SearchState.NotFoundException, viewModel.searchState.value)
            assertEquals(0, searchRepo.isbnCalls)
        }

    @Test
    fun `searchByIsbn NotFoundException flag takes precedence over null isbn`() =
        runTest {
            viewModel.searchByIsbn(null, isNotFoundException = true)

            assertEquals(SearchState.NotFoundException, viewModel.searchState.value)
        }

    @Test
    fun `searchByIsbn sets Loading immediately`() =
        runTest {
            viewModel.searchByIsbn("9782070360024")

            assertEquals(SearchState.Loading, viewModel.searchState.value)
        }

    @Test
    fun `searchByIsbn emits Success with results`() =
        runTest {
            searchRepo.isbnResults = listOf(dune)

            viewModel.searchByIsbn("9782070360024")
            advanceUntilIdle()

            val state = assertIs<SearchState.Success>(viewModel.searchState.value)
            assertEquals(listOf(dune), state.results)
            assertEquals("9782070360024", searchRepo.lastIsbn)
        }

    @Test
    fun `searchByIsbn emits Empty when no results`() =
        runTest {
            searchRepo.isbnResults = emptyList()

            viewModel.searchByIsbn("9782070360024")
            advanceUntilIdle()

            assertEquals(SearchState.Empty, viewModel.searchState.value)
        }

    @Test
    fun `searchByIsbn emits Error when use case throws`() =
        runTest {
            searchRepo.shouldFail = true

            viewModel.searchByIsbn("9782070360024")
            advanceUntilIdle()

            assertEquals(SearchState.Error, viewModel.searchState.value)
        }

    @Test
    fun `searchByIsbn is ignored while a search is already loading`() =
        runTest {
            val gate = CompletableDeferred<Unit>()
            searchRepo.gate = gate
            searchRepo.isbnResults = listOf(dune)

            viewModel.searchByIsbn("111")
            advanceUntilIdle()
            assertEquals(SearchState.Loading, viewModel.searchState.value)

            viewModel.searchByIsbn(null)
            viewModel.searchByIsbn("222", isNotFoundException = true)
            advanceUntilIdle()

            assertEquals(SearchState.Loading, viewModel.searchState.value)
            assertEquals(1, searchRepo.isbnCalls)
            assertEquals("111", searchRepo.lastIsbn)

            gate.complete(Unit)
            advanceUntilIdle()

            assertIs<SearchState.Success>(viewModel.searchState.value)
        }

    @Test
    fun `search is ignored while an isbn search is loading`() =
        runTest {
            val gate = CompletableDeferred<Unit>()
            searchRepo.gate = gate

            viewModel.searchByIsbn("111")
            advanceUntilIdle()

            viewModel.search("dune", ItemType.LITERATURE)
            advanceUntilIdle()

            assertEquals(0, searchRepo.searchCalls)

            gate.complete(Unit)
            advanceUntilIdle()
        }

    // ── Preferences ────────────────────────────────────────────────────────────

    @Test
    fun `preferences expose default values`() {
        assertTrue(viewModel.saveEnglishDescription.value)
        assertTrue(viewModel.replaceTitle.value)
    }

    @Test
    fun `setSaveEnglishDescription updates flow and persists`() {
        viewModel.setSaveEnglishDescription(false)

        assertFalse(viewModel.saveEnglishDescription.value)
        assertFalse(userPrefs.saveEnglishDescription)
        assertEquals(false, settings.getBooleanOrNull("save_english_description"))

        viewModel.setSaveEnglishDescription(true)

        assertTrue(viewModel.saveEnglishDescription.value)
    }

    @Test
    fun `replaceTitle reflects repository changes`() {
        userPrefs.replaceTitle = false

        assertFalse(viewModel.replaceTitle.value)
    }

    // ── existsByTitleAndType ───────────────────────────────────────────────────

    @Test
    fun `existsByTitleAndType returns true when repository finds a duplicate`() =
        runTest {
            collectionRepo.exists = true

            val result = viewModel.existsByTitleAndType("Naruto", ItemType.MANGA, excludeId = 5L)

            assertTrue(result)
            assertEquals("Naruto", collectionRepo.lastTitle)
            assertEquals(ItemType.MANGA, collectionRepo.lastType)
            assertEquals(5L, collectionRepo.lastExcludeId)
        }

    @Test
    fun `existsByTitleAndType returns false when no duplicate`() =
        runTest {
            collectionRepo.exists = false

            assertFalse(viewModel.existsByTitleAndType("Dune", ItemType.LITERATURE, excludeId = 0L))
        }
}
