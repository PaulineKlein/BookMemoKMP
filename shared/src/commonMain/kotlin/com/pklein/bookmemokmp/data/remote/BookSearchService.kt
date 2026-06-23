package com.pklein.bookmemokmp.data.remote

import com.pklein.bookmemokmp.androidCertFingerprint
import com.pklein.bookmemokmp.androidPackageName
import com.pklein.bookmemokmp.data.remote.dto.GoogleBooksResponse
import com.pklein.bookmemokmp.data.remote.dto.JikanAnimeResponse
import com.pklein.bookmemokmp.data.remote.dto.JikanAnimeSingleResponse
import com.pklein.bookmemokmp.data.remote.dto.JikanMangaResponse
import com.pklein.bookmemokmp.data.remote.dto.JikanMangaSingleResponse
import com.pklein.bookmemokmp.data.remote.mapper.toSearchResults
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.googleBooksApiKey
import com.pklein.bookmemokmp.isDebugBuild
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val SEARCH_RESULTS_LIMIT = 10
private const val SEARCH_TOP_RESULTS_LIMIT = 25

class BookSearchService {
    private val json = Json { ignoreUnknownKeys = true }

    private val client =
        HttpClient {
            expectSuccess = true // throw on 4xx/5xx so runCatching can catch them
            install(ContentNegotiation) { json(json) }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000 // 15 seconds
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }
            install(Logging) {
                logger = platformKtorLogger()
                level = if (isDebugBuild()) LogLevel.ALL else LogLevel.NONE
            }
        }

    // ── Google Books ──────────────────────────────────────────────────────────

    suspend fun searchGoogleBooks(
        query: String,
        langRestrict: String? = null,
    ): List<SearchResult> {
        val response: GoogleBooksResponse =
            client
                .get("https://www.googleapis.com/books/v1/volumes") {
                    parameter("q", query)
                    parameter("maxResults", SEARCH_RESULTS_LIMIT)
                    langRestrict?.let { parameter("langRestrict", it) }
                    // TODO NEED to create an API key for iOS too
                    googleBooksApiKey()?.let { parameter("key", it) }
                    androidPackageName()?.let { header("X-Android-Package", it) }
                    androidCertFingerprint()?.let { header("X-Android-Cert", it) }
                }.body()
        return response.toSearchResults()
    }

    // ── Jikan ─────────────────────────────────────────────────────────────────

    /**
     * Queries both manga and anime endpoints, merges and deduplicates by title
     * (manga results first), returns up to [SEARCH_RESULTS_LIMIT].
     */
    suspend fun searchJikanManga(query: String): List<SearchResult> {
        val response: JikanMangaResponse =
            client
                .get("https://api.jikan.moe/v4/manga") {
                    parameter("q", query)
                    parameter("limit", SEARCH_RESULTS_LIMIT)
                    parameter("sfw", true) // Filter out Adult entries
                }.body()
        return response.toSearchResults()
    }

    suspend fun searchJikanAnime(query: String): List<SearchResult> {
        val response: JikanAnimeResponse =
            client
                .get("https://api.jikan.moe/v4/anime") {
                    parameter("q", query)
                    parameter("limit", SEARCH_RESULTS_LIMIT)
                    parameter("sfw", true) // Filter out Adult entries
                }.body()
        return response.toSearchResults()
    }

    suspend fun fetchTopManga(page: Int = 1): Pair<List<SearchResult>, Boolean> {
        val response: JikanMangaResponse =
            client
                .get("https://api.jikan.moe/v4/top/manga") {
                    parameter("limit", SEARCH_TOP_RESULTS_LIMIT)
                    parameter("page", page)
                    parameter("sfw", true) // Filter out Adult entries
                }.body()
        return response.toSearchResults() to (response.pagination?.hasNextPage ?: false)
    }

    suspend fun fetchMangaUpdate(jikanId: Long): JikanUpdateResult {
        val response: JikanMangaSingleResponse =
            client
                .get("https://api.jikan.moe/v4/manga/$jikanId/full")
                .body()
        val item = response.data
        return JikanUpdateResult(
            totTome = item?.volumes,
            totChapter = item?.chapters,
            totEpisode = null,
        )
    }

    suspend fun fetchAnimeUpdate(jikanId: Long): JikanUpdateResult {
        val response: JikanAnimeSingleResponse =
            client
                .get("https://api.jikan.moe/v4/anime/$jikanId/full")
                .body()
        val item = response.data
        return JikanUpdateResult(
            totTome = null,
            totChapter = null,
            totEpisode = item?.episodes,
        )
    }
}

data class JikanUpdateResult(
    val totTome: Int?,
    val totChapter: Int?,
    val totEpisode: Int?,
)
