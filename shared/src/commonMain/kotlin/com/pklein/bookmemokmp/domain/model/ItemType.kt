package com.pklein.bookmemokmp.domain.model

enum class ItemType {
    LITERATURE,
    MANGA,
    COMIC,
    ;

    companion object {
        fun fromString(value: String): ItemType = entries.firstOrNull { it.name == value } ?: LITERATURE
    }
}
