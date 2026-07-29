package com.pklein.bookmemokmp.data.repository

import com.pklein.bookmemokmp.data.remote.BookSearchService
import com.pklein.bookmemokmp.data.remote.SEARCH_BIG_RESULTS_LIMIT
import com.pklein.bookmemokmp.data.remote.SEARCH_LAW_RESULTS_LIMIT
import com.pklein.bookmemokmp.data.remote.UpdateResult
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.domain.repository.IBookSearchRepository

class BookSearchRepository(
    private val searchService: BookSearchService,
) : IBookSearchRepository {
    override suspend fun searchByIsbn(isbn: String): List<SearchResult> =
        searchService.searchGoogleBooks(
            query = "isbn:$isbn",
            searchResultLimit = SEARCH_LAW_RESULTS_LIMIT,
            langRestrict = null,
        )

    /**
     * Search online for book/manga/anime info matching [query].
     * - LITERATURE / COMIC → Google Books API
     * - MANGA → My anime list v2 (manga + anime, deduplicated)
     */
    override suspend fun search(
        query: String,
        type: ItemType,
        langRestrict: String?,
    ): List<SearchResult> =
        when (type) {
            ItemType.LITERATURE, ItemType.COMIC -> {
                searchService.searchGoogleBooks(
                    query = query,
                    searchResultLimit = SEARCH_LAW_RESULTS_LIMIT,
                    langRestrict = langRestrict,
                )
            }

            ItemType.MANGA -> {
                searchMangaAndAnime(query)
            }
        }

    private suspend fun searchMangaAndAnime(query: String): List<SearchResult> {
        val manga = runCatching { searchService.searchMangaApi(query) }.getOrDefault(emptyList())
        val anime = runCatching { searchService.searchAnimeApi(query) }.getOrDefault(emptyList())

        val seen = mutableSetOf<String>()
        return (manga + anime).filter { seen.add(it.title.trim().lowercase()) }
    }

    override suspend fun fetchMoreBooksFromAuthor(
        type: ItemType,
        author: String,
        authorId: Long?,
        langRestrict: String?,
    ): List<SearchResult> {
        // Use only the first listed author with inauthor: so Google Books filters properly.
        // Multi-author strings like "Kishimoto, Ikemoto" would return results for all of them.
        val primaryAuthor = author.split(",").first().trim()
        return searchService
            .searchGoogleBooks(
                query = "inauthor:\"$primaryAuthor\"",
                searchResultLimit = SEARCH_BIG_RESULTS_LIMIT,
                langRestrict = langRestrict,
            ).deduplicateBySeries()
    }

    override suspend fun fetchTopManga(
        page: Int,
        rankingType: String,
    ): Pair<List<SearchResult>, Boolean> = searchService.fetchTopManga(page, rankingType)

    override suspend fun fetchTopAnime(page: Int): Pair<List<SearchResult>, Boolean> = searchService.fetchTopAnime(page)

    override suspend fun fetchMangaUpdate(malId: Long): UpdateResult = searchService.fetchMangaUpdate(malId)

    override suspend fun fetchAnimeUpdate(malId: Long): UpdateResult = searchService.fetchAnimeUpdate(malId)
}

private fun List<SearchResult>.deduplicateBySeries(): List<SearchResult> {
    // Strips volume/tome suffixes then deduplicates, keeping the first occurrence of each series.
    // Handles: "Vol. 3", "tome 5", "#12", "no 06/20", "n°3", "- 4", "(Book 2)", bare trailing numbers.
    val volumeSuffixRegex =
        Regex(
            """[,\s\-–]+(?:vol(?:ume)?\.?|tome|t\.|no\.?|n°|#|book|bd\.?|volume)\s*\d+[\d/]*.*$""" +
                """|[(\s\-–]+\d+(?:/\d+)?[)\s]*$""",
            RegexOption.IGNORE_CASE,
        )
    val seen = mutableSetOf<String>()
    return filter { result ->
        val seriesKey = volumeSuffixRegex.replace(result.title, "").trim().lowercase()
        seen.add(seriesKey)
    }
}
