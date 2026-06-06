package com.pklein.bookmemokmp.domain.model

data class CollectionItem(
    val id: Long = 0,
    val type: ItemType,
    val title: String,
    val author: String? = null,
    val illustrator: String? = null,
    val year: Int? = null,
    val bought: Boolean = false,
    val wishlist: Boolean = false,
    val finished: Boolean = false,
    val tome: Int? = null, // last tome read
    val chapter: Int? = null, // last chapter read
    val episode: Int? = null, // last episode watched
    val season: Int? = null, // last season watched
    val description: String? = null,
    val favorite: Boolean = false,
    val imageUrl: String? = null,
    val isBorrowed: Boolean = false,
    val borrowedSince: Long? = null, // Unix timestamp in milliseconds
    val borrowedBy: String? = null,
    val jikanId: Long? = null,
    val jikanType: JikanType? = null,
    val totTome: Int? = null,
    val totChapter: Int? = null,
    val totEpisode: Int? = null,
)
