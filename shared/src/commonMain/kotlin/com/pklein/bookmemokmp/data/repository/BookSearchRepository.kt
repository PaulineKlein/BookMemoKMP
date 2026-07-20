package com.pklein.bookmemokmp.data.repository

import com.pklein.bookmemokmp.data.remote.BookSearchService
import com.pklein.bookmemokmp.data.remote.JikanUpdateResult
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.domain.repository.IBookSearchRepository

class BookSearchRepository(
    private val searchService: BookSearchService,
) : IBookSearchRepository {
    override suspend fun searchByIsbn(isbn: String): List<SearchResult> = searchService.searchGoogleBooks("isbn:$isbn", langRestrict = null)

    /**
     * Search online for book/manga/anime info matching [query].
     * - LITERATURE / COMIC → Google Books API
     * - MANGA              → Jikan v4 (manga + anime, deduplicated)
     */
    override suspend fun search(
        query: String,
        type: ItemType,
        langRestrict: String?,
    ): List<SearchResult> =
        when (type) {
            ItemType.LITERATURE, ItemType.COMIC -> searchService.searchGoogleBooks(query, langRestrict)
            ItemType.MANGA -> searchMangaAndAnime(query)
        }

    private suspend fun searchMangaAndAnime(query: String): List<SearchResult> {
        val manga = runCatching { searchService.searchJikanManga(query) }.getOrDefault(emptyList())
        val anime = runCatching { searchService.searchJikanAnime(query) }.getOrDefault(emptyList())

        val seen = mutableSetOf<String>()
        return (manga + anime).filter { seen.add(it.title.trim().lowercase()) }
    }

    override suspend fun fetchTopManga(page: Int): Pair<List<SearchResult>, Boolean> = searchService.fetchTopManga(page)

    override suspend fun fetchMangaUpdate(malId: Long): JikanUpdateResult = searchService.fetchMangaUpdate(malId)

    override suspend fun fetchAnimeUpdate(malId: Long): JikanUpdateResult = searchService.fetchAnimeUpdate(malId)
}
