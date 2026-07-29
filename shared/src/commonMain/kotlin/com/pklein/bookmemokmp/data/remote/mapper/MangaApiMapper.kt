package com.pklein.bookmemokmp.data.remote.mapper

import com.pklein.bookmemokmp.data.remote.dto.AnimeApiResponse
import com.pklein.bookmemokmp.data.remote.dto.MangaApiResponse
import com.pklein.bookmemokmp.domain.model.MangaApiType
import com.pklein.bookmemokmp.domain.model.SearchResult

internal fun MangaApiResponse.toSearchResults(): List<SearchResult> =
    data?.map { wrapper ->
        val item = wrapper.node
        val mainAuthors =
            item.authors?.filter {
                val r = it.role?.lowercase()
                r == "story" || r == "art" || r == "story & art"
            }
        SearchResult(
            title = item.title,
            author =
                mainAuthors
                    ?.mapNotNull { edge ->
                        listOfNotNull(edge.node?.firstName, edge.node?.lastName)
                            .joinToString(" ")
                            .ifBlank { null }
                    }?.joinToString(", ")
                    ?.ifBlank { null },
            year = item.startDate?.take(4)?.toIntOrNull(),
            description = item.synopsis?.cleanJikanSynopsis(),
            imageUrl = item.mainPicture?.large ?: item.mainPicture?.medium,
            mangaApiId = item.id.takeIf { it != 0L },
            mangaApiAuthorId = mainAuthors?.firstNotNullOfOrNull { it.node?.id?.takeIf { id -> id != 0L } },
            mangaApiType = MangaApiType.MANGA,
            totTome = item.numVolumes,
            totChapter = item.numChapters,
        )
    } ?: emptyList()

internal fun AnimeApiResponse.toSearchResults(): List<SearchResult> =
    data?.map { wrapper ->
        val item = wrapper.node
        SearchResult(
            title = item.title,
            author =
                item.studios
                    ?.mapNotNull { it.node?.name?.ifBlank { null } }
                    ?.joinToString(", ")
                    ?.ifBlank { null },
            year = item.startDate?.take(4)?.toIntOrNull(),
            description = item.synopsis?.cleanJikanSynopsis(),
            imageUrl = item.mainPicture?.large ?: item.mainPicture?.medium,
            mangaApiId = item.id.takeIf { it != 0L },
            mangaApiType = MangaApiType.ANIME,
            totEpisode = item.numEpisodes,
        )
    } ?: emptyList()

// MAL synopses often end with editorial tags like "[Written by MAL Rewrite]".
private val malTagRegex = Regex("""\s*\[[^]]*(?:MAL|Rewrite)[^]]*]""", RegexOption.IGNORE_CASE)

private fun String.cleanJikanSynopsis(): String? = malTagRegex.replace(this, "").trim().ifEmpty { null }
