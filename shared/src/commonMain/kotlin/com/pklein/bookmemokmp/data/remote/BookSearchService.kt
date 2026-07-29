package com.pklein.bookmemokmp.data.remote

import com.pklein.bookmemokmp.androidCertFingerprint
import com.pklein.bookmemokmp.androidPackageName
import com.pklein.bookmemokmp.data.remote.dto.AnimeApiResponse
import com.pklein.bookmemokmp.data.remote.dto.AnimeApiSingleResponse
import com.pklein.bookmemokmp.data.remote.dto.GoogleBooksResponse
import com.pklein.bookmemokmp.data.remote.dto.MangaApiResponse
import com.pklein.bookmemokmp.data.remote.dto.MangaApiSingleResponse
import com.pklein.bookmemokmp.data.remote.mapper.toSearchResults
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.googleBooksApiKey
import com.pklein.bookmemokmp.isDebugBuild
import com.pklein.bookmemokmp.mangaApiKey
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

const val SEARCH_LAW_RESULTS_LIMIT = 10
const val SEARCH_BIG_RESULTS_LIMIT = 40
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
        searchResultLimit: Int,
        langRestrict: String? = null,
    ): List<SearchResult> {
        val response: GoogleBooksResponse =
            client
                .get("https://www.googleapis.com/books/v1/volumes") {
                    parameter("q", query)
                    parameter("maxResults", searchResultLimit)
                    langRestrict?.let { parameter("langRestrict", it) }
                    // TODO NEED to create an API key for iOS too
                    googleBooksApiKey()?.let { parameter("key", it) }
                    androidPackageName()?.let { header("X-Android-Package", it) }
                    androidCertFingerprint()?.let { header("X-Android-Cert", it) }
                }.body()
        return response.toSearchResults()
    }

    // ── My Anime List API ─────────────────────────────────────────────────────────────────

    /**
     * Queries both manga and anime endpoints, merges and deduplicates by title
     * (manga results first), returns up to [SEARCH_LAW_RESULTS_LIMIT].
     */
    suspend fun searchMangaApi(query: String): List<SearchResult> {
        val response: MangaApiResponse =
            client
                .get("https://api.myanimelist.net/v2/manga") {
                    // TODO NEED to create an API key for iOS too
                    mangaApiKey()?.let { header("X-MAL-CLIENT-ID", it) }
                    parameter("q", query)
                    parameter("limit", SEARCH_LAW_RESULTS_LIMIT)
                    parameter("nsfw", false)
                    parameter(
                        "fields",
                        "synopsis,num_volumes,num_chapters,status,start_date,authors{id,first_name,last_name}",
                    )
                }.body()
        return response.toSearchResults()
    }

    suspend fun searchAnimeApi(query: String): List<SearchResult> {
        val response: AnimeApiResponse =
            client
                .get("https://api.myanimelist.net/v2/anime") {
                    mangaApiKey()?.let { header("X-MAL-CLIENT-ID", it) }
                    parameter("q", query)
                    parameter("limit", SEARCH_LAW_RESULTS_LIMIT)
                    parameter("nsfw", false) // Filter out Adult entries
                    parameter("fields", "synopsis,num_episodes,status,start_date,studios{name}")
                }.body()
        return response.toSearchResults()
    }

    suspend fun fetchTopManga(
        page: Int = 1,
        rankingType: String,
    ): Pair<List<SearchResult>, Boolean> {
        val offset = (page - 1) * SEARCH_TOP_RESULTS_LIMIT
        val response: MangaApiResponse =
            client
                .get("https://api.myanimelist.net/v2/manga/ranking") {
                    mangaApiKey()?.let { header("X-MAL-CLIENT-ID", it) }
                    parameter("ranking_type", rankingType)
                    parameter("limit", SEARCH_TOP_RESULTS_LIMIT)
                    parameter("offset", offset)
                    parameter("nsfw", false) // Filter out Adult entries
                    parameter(
                        "fields",
                        "synopsis,num_volumes,num_chapters,status,start_date,authors{id,first_name,last_name}",
                    )
                }.body()
        return response.toSearchResults() to (response.pagination?.hasNextPage ?: false)
    }

    suspend fun fetchTopAnime(page: Int = 1): Pair<List<SearchResult>, Boolean> {
        val offset = (page - 1) * SEARCH_TOP_RESULTS_LIMIT
        val response: AnimeApiResponse =
            client
                .get("https://api.myanimelist.net/v2/anime/ranking") {
                    mangaApiKey()?.let { header("X-MAL-CLIENT-ID", it) }
                    parameter("ranking_type", "all")
                    parameter("limit", SEARCH_TOP_RESULTS_LIMIT)
                    parameter("offset", offset)
                    parameter("nsfw", false) // Filter out Adult entries
                    parameter("fields", "synopsis,num_episodes,status,start_date,studios{name}")
                }.body()
        return response.toSearchResults() to (response.pagination?.hasNextPage ?: false)
    }

    suspend fun fetchMangaUpdate(id: Long): UpdateResult {
        val response: MangaApiSingleResponse =
            client
                .get("https://api.myanimelist.net/v2/manga/$id") {
                    mangaApiKey()?.let { header("X-MAL-CLIENT-ID", it) }
                    parameter("fields", "num_volumes,num_chapters,authors{id,first_name,last_name}")
                }.body()
        val mainAuthorId = response.firstStoryArtAuthorId()
        return UpdateResult(
            totTome = response.volumes,
            totChapter = response.chapters,
            totEpisode = null,
            mangaApiAuthorId = mainAuthorId,
        )
    }

    suspend fun fetchAnimeUpdate(id: Long): UpdateResult {
        val response: AnimeApiSingleResponse =
            client
                .get("https://api.myanimelist.net/v2/anime/$id") {
                    mangaApiKey()?.let { header("X-MAL-CLIENT-ID", it) }
                    parameter("fields", "num_episodes")
                }.body()
        return UpdateResult(
            totTome = null,
            totChapter = null,
            totEpisode = response.episodes,
        )
    }
}

data class UpdateResult(
    val totTome: Int?,
    val totChapter: Int?,
    val totEpisode: Int?,
    val mangaApiAuthorId: Long? = null,
)
