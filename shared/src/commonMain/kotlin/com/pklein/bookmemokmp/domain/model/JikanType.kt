package com.pklein.bookmemokmp.domain.model

enum class JikanType(
    val value: String,
) {
    MANGA("manga"),
    ANIME("anime"),
    ;

    companion object {
        fun fromString(value: String?): JikanType? = JikanType.entries.firstOrNull { it.value == value }
    }
}
