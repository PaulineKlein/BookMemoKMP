package com.pklein.bookmemokmp.presentation.collection.filter

import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.filter_all
import bookmemokmp.shared.generated.resources.filter_book
import bookmemokmp.shared.generated.resources.filter_manga
import bookmemokmp.shared.generated.resources.type_comic
import com.pklein.bookmemokmp.domain.model.ItemType
import org.jetbrains.compose.resources.StringResource

enum class CollectionFilter(val label: StringResource) {
    ALL(Res.string.filter_all),
    LITERATURE(Res.string.filter_book),
    MANGA(Res.string.filter_manga),
    COMIC(Res.string.type_comic);

    /** The corresponding domain type, or null for ALL. */
    val itemType: ItemType?
        get() = when (this) {
            ALL -> null
            LITERATURE -> ItemType.LITERATURE
            MANGA -> ItemType.MANGA
            COMIC -> ItemType.COMIC
        }
}
