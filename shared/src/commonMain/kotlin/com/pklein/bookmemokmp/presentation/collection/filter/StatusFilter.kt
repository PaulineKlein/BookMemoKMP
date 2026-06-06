package com.pklein.bookmemokmp.presentation.collection.filter

import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.bought
import bookmemokmp.shared.generated.resources.filter_fav
import bookmemokmp.shared.generated.resources.filter_not_bought
import bookmemokmp.shared.generated.resources.filter_not_fav
import bookmemokmp.shared.generated.resources.filter_not_finished
import bookmemokmp.shared.generated.resources.filter_not_in_wishlist
import bookmemokmp.shared.generated.resources.filter_not_loaned
import bookmemokmp.shared.generated.resources.filter_wishlist
import bookmemokmp.shared.generated.resources.finished
import bookmemokmp.shared.generated.resources.loaned
import org.jetbrains.compose.resources.StringResource

enum class TriState { ALL, YES, NO }

data class StatusFilters(
    val favorites: TriState = TriState.ALL,
    val wishlist: TriState = TriState.ALL,
    val bought: TriState = TriState.ALL,
    val finished: TriState = TriState.ALL,
    val loan: TriState = TriState.ALL,
) {
    val isDefault get() = this == StatusFilters()
}

enum class StatusFilterField(
    val labelYes: StringResource,
    val labelNo: StringResource,
    val get: (StatusFilters) -> TriState,
    val set: (StatusFilters, TriState) -> StatusFilters
) {
    FAVORITES(
        labelYes = Res.string.filter_fav,
        labelNo = Res.string.filter_not_fav,
        get = { it.favorites },
        set = { f, v -> f.copy(favorites = v) }
    ),
    WISHLIST(
        labelYes = Res.string.filter_wishlist,
        labelNo = Res.string.filter_not_in_wishlist,
        get = { it.wishlist },
        set = { f, v -> f.copy(wishlist = v) }
    ),
    BOUGHT(
        labelYes = Res.string.bought,
        labelNo = Res.string.filter_not_bought,
        get = { it.bought },
        set = { f, v -> f.copy(bought = v) }
    ),
    FINISHED(
        labelYes = Res.string.finished,
        labelNo = Res.string.filter_not_finished,
        get = { it.finished },
        set = { f, v -> f.copy(finished = v) }
    ),
    LOAN(
        labelYes = Res.string.loaned,
        labelNo = Res.string.filter_not_loaned,
        get = { it.loan },
        set = { f, v -> f.copy(loan = v) }
    );

    fun cycle(filters: StatusFilters): StatusFilters {
        val next = when (get(filters)) {
            TriState.ALL -> TriState.YES
            TriState.YES -> TriState.NO
            TriState.NO -> TriState.ALL
        }
        return set(filters, next)
    }
}
