package com.pklein.bookmemokmp.domain.model

enum class MangaApiType(
    val value: String,
) {
    MANGA("manga"),
    ANIME("anime"),
    ;

    companion object {
        fun fromString(value: String?): MangaApiType? = MangaApiType.entries.firstOrNull { it.value == value }
    }
}
