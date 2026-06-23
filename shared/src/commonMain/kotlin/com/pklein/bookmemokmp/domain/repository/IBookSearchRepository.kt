package com.pklein.bookmemokmp.domain.repository

import com.pklein.bookmemokmp.data.remote.JikanUpdateResult
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult

interface IBookSearchRepository {
    suspend fun searchByIsbn(isbn: String): List<SearchResult>
    suspend fun search(query: String, type: ItemType, langRestrict: String? = null): List<SearchResult>
    suspend fun fetchTopManga(page: Int = 1): Pair<List<SearchResult>, Boolean>
    suspend fun fetchMangaUpdate(malId: Long): JikanUpdateResult
    suspend fun fetchAnimeUpdate(malId: Long): JikanUpdateResult
}
