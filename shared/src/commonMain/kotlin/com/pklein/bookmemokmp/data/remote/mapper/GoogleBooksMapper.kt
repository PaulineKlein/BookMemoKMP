package com.pklein.bookmemokmp.data.remote.mapper

import com.pklein.bookmemokmp.data.remote.dto.GoogleBooksResponse
import com.pklein.bookmemokmp.domain.model.SearchResult

internal fun GoogleBooksResponse.toSearchResults(): List<SearchResult> =
    items?.map { volume ->
        SearchResult(
            title = volume.volumeInfo.title,
            author = volume.volumeInfo.authors?.joinToString(", "),
            year =
                volume.volumeInfo.publishedDate
                    ?.take(4)
                    ?.toIntOrNull(),
            description = volume.volumeInfo.description,
            imageUrl =
                volume.volumeInfo.imageLinks
                    ?.thumbnail
                    ?.replace("http://", "https://"),
        )
    } ?: emptyList()
