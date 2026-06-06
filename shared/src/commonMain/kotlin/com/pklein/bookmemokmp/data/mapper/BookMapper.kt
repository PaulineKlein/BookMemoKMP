package com.pklein.bookmemokmp.data.mapper

import com.pklein.bookmemokmp.database.Book
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.JikanType

fun Book.toDomain(): CollectionItem =
    CollectionItem(
        id = id,
        type = ItemType.fromString(type),
        title = title,
        author = author,
        illustrator = illustrator,
        year = year?.toInt(),
        bought = bought == 1L,
        wishlist = wishlist == 1L,
        finished = finish == 1L,
        tome = tome?.toInt(),
        chapter = chapter?.toInt(),
        episode = episode?.toInt(),
        season = season?.toInt(),
        description = desc,
        favorite = favorite == 1L,
        imageUrl = image_url,
        isBorrowed = is_borrowed == 1L,
        borrowedSince = borrowed_since,
        borrowedBy = borrowed_by,
        jikanId = jikan_id,
        jikanType = JikanType.fromString(jikan_type),
        totTome = tot_tome?.toInt(),
        totChapter = tot_chapter?.toInt(),
        totEpisode = tot_episode?.toInt(),
    )
