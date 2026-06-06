package com.pklein.bookmemokmp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Manga ─────────────────────────────────────────────────────────────────────

@Serializable
internal data class JikanMangaResponse(
    val data: List<JikanMangaItem>? = null,
    val pagination: JikanPagination? = null,
)

@Serializable
internal data class JikanPagination(
    @SerialName("has_next_page") val hasNextPage: Boolean = false,
)

@Serializable
internal data class JikanMangaItem(
    @SerialName("mal_id") val malId: Long = 0,
    val title: String = "",
    val synopsis: String? = null,
    val authors: List<JikanPerson>? = null,
    val published: JikanPublished? = null,
    val images: JikanImages? = null,
    val volumes: Int? = null,
    val chapters: Int? = null,
    val status: String? = null,
)

@Serializable
internal data class JikanMangaSingleResponse(
    val data: JikanMangaItem? = null,
)

@Serializable
internal data class JikanPerson(
    val name: String = "",
)

@Serializable
internal data class JikanPublished(
    val prop: JikanPublishedProp? = null,
)

@Serializable
internal data class JikanPublishedProp(
    val from: JikanDate? = null,
)

@Serializable
internal data class JikanDate(
    val year: Int? = null,
)

// ── Anime ─────────────────────────────────────────────────────────────────────

@Serializable
internal data class JikanAnimeResponse(
    val data: List<JikanAnimeItem>? = null,
)

@Serializable
internal data class JikanAnimeItem(
    @SerialName("mal_id") val malId: Long = 0,
    val title: String = "",
    val synopsis: String? = null,
    val studios: List<JikanStudio>? = null,
    val year: Int? = null,
    val images: JikanImages? = null,
    val episodes: Int? = null,
    val status: String? = null,
)

@Serializable
internal data class JikanAnimeSingleResponse(
    val data: JikanAnimeItem? = null,
)

@Serializable
internal data class JikanStudio(
    val name: String = "",
)

// ── Shared image structures ───────────────────────────────────────────────────

@Serializable
internal data class JikanImages(
    val jpg: JikanImageFormat? = null,
    val webp: JikanImageFormat? = null,
)

@Serializable
internal data class JikanImageFormat(
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("small_image_url") val smallImageUrl: String? = null,
    @SerialName("large_image_url") val largeImageUrl: String? = null,
)
