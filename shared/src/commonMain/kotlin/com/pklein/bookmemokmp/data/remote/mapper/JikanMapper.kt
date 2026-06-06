package com.pklein.bookmemokmp.data.remote.mapper

import com.pklein.bookmemokmp.data.remote.dto.JikanAnimeResponse
import com.pklein.bookmemokmp.data.remote.dto.JikanMangaResponse
import com.pklein.bookmemokmp.domain.model.JikanType
import com.pklein.bookmemokmp.domain.model.SearchResult

internal fun JikanMangaResponse.toSearchResults(): List<SearchResult> =
    data?.map { item ->
        SearchResult(
            title = item.title,
            author = item.authors?.joinToString(", ") { it.name },
            year =
                item.published
                    ?.prop
                    ?.from
                    ?.year,
            description = item.synopsis?.cleanJikanSynopsis(),
            imageUrl = item.images?.jpg?.largeImageUrl ?: item.images?.jpg?.imageUrl,
            jikanId = item.malId.takeIf { it != 0L },
            jikanType = JikanType.MANGA,
            totTome = item.volumes,
            totChapter = item.chapters,
        )
    } ?: emptyList()

internal fun JikanAnimeResponse.toSearchResults(): List<SearchResult> =
    data?.map { item ->
        SearchResult(
            title = item.title,
            author = item.studios?.joinToString(", ") { it.name },
            year = item.year,
            description = item.synopsis?.cleanJikanSynopsis(),
            imageUrl = item.images?.jpg?.largeImageUrl ?: item.images?.jpg?.imageUrl,
            jikanId = item.malId.takeIf { it != 0L },
            jikanType = JikanType.ANIME,
            totEpisode = item.episodes,
        )
    } ?: emptyList()

// MAL synopses often end with editorial tags like "[Written by MAL Rewrite]".
private val malTagRegex = Regex("""\s*\[[^]]*(?:MAL|Rewrite)[^]]*]""", RegexOption.IGNORE_CASE)

private fun String.cleanJikanSynopsis(): String? = malTagRegex.replace(this, "").trim().ifEmpty { null }
