package com.pklein.bookmemokmp.domain.repository

import com.pklein.bookmemokmp.data.remote.UpdateResult
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult

interface IBookSearchRepository {
    suspend fun searchByIsbn(isbn: String): List<SearchResult>

    suspend fun search(
        query: String,
        type: ItemType,
        langRestrict: String? = null,
    ): List<SearchResult>

    suspend fun fetchMoreBooksFromAuthor(
        type: ItemType,
        author: String,
        authorId: Long? = null,
        langRestrict: String? = null,
    ): List<SearchResult>

    suspend fun fetchTopManga(
        page: Int = 1,
        rankingType: String,
    ): Pair<List<SearchResult>, Boolean>

    suspend fun fetchTopAnime(page: Int = 1): Pair<List<SearchResult>, Boolean>

    suspend fun fetchMangaUpdate(malId: Long): UpdateResult

    suspend fun fetchAnimeUpdate(malId: Long): UpdateResult
}
