package com.pklein.bookmemokmp.domain.usecase

import com.pklein.bookmemokmp.data.remote.UpdateResult
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.MangaApiType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.domain.repository.IBookSearchRepository

class BookSearchUseCase(
    private val repository: IBookSearchRepository,
) {
    suspend fun search(
        query: String,
        type: ItemType,
        langRestrict: String? = null,
    ): List<SearchResult> = repository.search(query, type, langRestrict)

    suspend fun searchByIsbn(isbn: String): List<SearchResult> = repository.searchByIsbn(isbn)

    suspend fun fetchMoreBooksFromAuthor(
        type: ItemType,
        author: String,
        authorId: Long? = null,
        langRestrict: String? = null,
    ): List<SearchResult> = repository.fetchMoreBooksFromAuthor(type, author, authorId, langRestrict)

    suspend fun fetchTopManga(page: Int = 1): Pair<List<SearchResult>, Boolean> = repository.fetchTopManga(page)

    suspend fun checkForUpdates(
        jikanId: Long,
        mangaApiType: MangaApiType,
    ): UpdateResult =
        when (mangaApiType) {
            MangaApiType.ANIME -> repository.fetchAnimeUpdate(jikanId)
            MangaApiType.MANGA -> repository.fetchMangaUpdate(jikanId)
        }
}
