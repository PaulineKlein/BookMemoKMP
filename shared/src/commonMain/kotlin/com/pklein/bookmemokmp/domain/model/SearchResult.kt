package com.pklein.bookmemokmp.domain.model

/**
 * A lightweight result returned by the online book/manga/anime search.
 * Used to pre-fill the AddItemScreen form.
 */
data class SearchResult(
    val title: String,
    val author: String?,
    val year: Int?,
    val description: String?,
    val imageUrl: String? = null,
    val jikanId: Long? = null,
    val jikanType: JikanType? = null,
    val totTome: Int? = null,
    val totChapter: Int? = null,
    val totEpisode: Int? = null,
)
