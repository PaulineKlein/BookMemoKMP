package com.pklein.bookmemokmp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Search responses ──────────────────────────────────────────────────────────

@Serializable
internal data class MangaApiResponse(
    val data: List<MangaApiNodeWrapper<MangaApiMangaNode>>? = null,
    val paging: MangaApiPaging? = null,
) {
    val pagination: MangaApiPagination?
        get() =
            if (paging?.next != null) {
                MangaApiPagination(hasNextPage = true)
            } else {
                MangaApiPagination(
                    hasNextPage = false,
                )
            }
}

@Serializable
internal data class AnimeApiResponse(
    val data: List<MangaApiNodeWrapper<MangaApiAnimeNode>>? = null,
    val paging: MangaApiPaging? = null,
) {
    val pagination: MangaApiPagination?
        get() =
            if (paging?.next != null) {
                MangaApiPagination(hasNextPage = true)
            } else {
                MangaApiPagination(
                    hasNextPage = false,
                )
            }
}

@Serializable
internal data class MangaApiPagination(
    @SerialName("has_next_page") val hasNextPage: Boolean = false,
)

@Serializable
internal data class MangaApiPaging(
    val next: String? = null,
)

@Serializable
internal data class MangaApiNodeWrapper<T>(
    val node: T,
)

// ── Manga node ────────────────────────────────────────────────────────────────

@Serializable
internal data class MangaApiMangaNode(
    val id: Long = 0,
    val title: String = "",
    @SerialName("main_picture") val mainPicture: MangaApiPicture? = null,
    val synopsis: String? = null,
    @SerialName("num_volumes") val numVolumes: Int? = null,
    @SerialName("num_chapters") val numChapters: Int? = null,
    val status: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    val authors: List<MangaApiAuthorEdge>? = null,
)

@Serializable
internal data class MangaApiAuthorEdge(
    val node: MangaApiAuthorNode? = null,
    val role: String? = null,
)

@Serializable
internal data class MangaApiAuthorNode(
    val id: Long = 0,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
)

// ── Anime node ────────────────────────────────────────────────────────────────

@Serializable
internal data class MangaApiAnimeNode(
    val id: Long = 0,
    val title: String = "",
    @SerialName("main_picture") val mainPicture: MangaApiPicture? = null,
    val synopsis: String? = null,
    @SerialName("num_episodes") val numEpisodes: Int? = null,
    val status: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    val studios: List<MangaApiStudioEdge>? = null,
)

@Serializable
internal data class MangaApiStudioEdge(
    val node: MangaApiStudioNode? = null,
)

@Serializable
internal data class MangaApiStudioNode(
    val name: String = "",
)

// ── Single-item detail responses (for update checks) ─────────────────────────

@Serializable
internal data class MangaApiSingleResponse(
    val id: Long = 0,
    @SerialName("num_volumes") val volumes: Int? = null,
    @SerialName("num_chapters") val chapters: Int? = null,
    val authors: List<MangaApiAuthorEdge>? = null,
) {
    val data: MangaApiSingleResponse get() = this

    fun firstStoryArtAuthorId(): Long? =
        authors
            ?.filter { edge ->
                val r = edge.role?.lowercase()
                r == "story" || r == "art" || r == "story & art"
            }?.firstNotNullOfOrNull { edge -> edge.node?.id?.takeIf { id -> id != 0L } }
}

@Serializable
internal data class AnimeApiSingleResponse(
    val id: Long = 0,
    @SerialName("num_episodes") val episodes: Int? = null,
) {
    val data: AnimeApiSingleResponse get() = this
}

// ── Shared picture ────────────────────────────────────────────────────────────

@Serializable
internal data class MangaApiPicture(
    val medium: String? = null,
    val large: String? = null,
)
