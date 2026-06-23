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
    val checkedTomes: List<Int> = emptyList(),
    val notes: String? = null,
    val format: FormatType? = null,
) {
    /** Compact range string of volumes absent from [checkedTomes] within 1..total,
     *  where total is [totTome] if known, otherwise the highest checked volume.
     *  Returns null when there is nothing to report.
     *  e.g. checkedTomes=[1,2,4], totTome=6  →  "3, 5-6" */
    fun missingTomesLabel(): String? {
        if (checkedTomes.isEmpty()) return null
        val total = if ((totTome ?: 0) > 0) totTome!! else (checkedTomes.maxOrNull() ?: 0)
        if (total <= 0) return null
        val checked = checkedTomes.toSet()
        val missing = (1..total).filter { it !in checked }
        if (missing.isEmpty()) return null
        return buildRangeString(missing)
    }

    private fun buildRangeString(numbers: List<Int>): String {
        val ranges = mutableListOf<String>()
        var start = numbers[0]
        var end = numbers[0]
        for (i in 1..numbers.lastIndex) {
            if (numbers[i] == end + 1) {
                end = numbers[i]
            } else {
                ranges += if (start == end) "$start" else "$start-$end"
                start = numbers[i]
                end = numbers[i]
            }
        }
        ranges += if (start == end) "$start" else "$start-$end"
        return ranges.joinToString(", ")
    }
}
