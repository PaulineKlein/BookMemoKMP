package com.pklein.bookmemokmp.domain.model

/** [missingTemplate] contains a "%s" placeholder replaced by the missing volumes range. */
data class ShareLabels(
    val author: String,
    val illustrator: String,
    val year: String,
    val volume: String,
    val chapter: String,
    val episode: String,
    val season: String,
    val missingTemplate: String,
)
