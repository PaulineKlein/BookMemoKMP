package com.pklein.bookmemokmp.presentation.collection.filter

import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.filter_book
import bookmemokmp.shared.generated.resources.filter_comic
import bookmemokmp.shared.generated.resources.filter_manga
import com.pklein.bookmemokmp.domain.model.ItemType
import org.jetbrains.compose.resources.StringResource

enum class CollectionFilter(
    val label: StringResource,
) {
    LITERATURE(Res.string.filter_book),
    MANGA(Res.string.filter_manga),
    COMIC(Res.string.filter_comic),
    ;

    val itemType: ItemType
        get() =
            when (this) {
                LITERATURE -> ItemType.LITERATURE
                MANGA -> ItemType.MANGA
                COMIC -> ItemType.COMIC
            }
}
