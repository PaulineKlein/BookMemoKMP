package com.pklein.bookmemokmp.domain.usecase

import com.pklein.bookmemokmp.data.remote.JikanUpdateResult
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.JikanType
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

    suspend fun fetchTopManga(page: Int = 1): Pair<List<SearchResult>, Boolean> = repository.fetchTopManga(page)

    suspend fun checkForUpdates(
        jikanId: Long,
        jikanType: JikanType,
    ): JikanUpdateResult =
        when (jikanType) {
            JikanType.ANIME -> repository.fetchAnimeUpdate(jikanId)
            JikanType.MANGA -> repository.fetchMangaUpdate(jikanId)
        }
}
