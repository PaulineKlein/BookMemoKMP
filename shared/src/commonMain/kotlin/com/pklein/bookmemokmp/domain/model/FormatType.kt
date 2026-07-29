package com.pklein.bookmemokmp.domain.model

import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.format_american_comics
import bookmemokmp.shared.generated.resources.format_anime
import bookmemokmp.shared.generated.resources.format_autobiography
import bookmemokmp.shared.generated.resources.format_biography
import bookmemokmp.shared.generated.resources.format_childrens
import bookmemokmp.shared.generated.resources.format_essay
import bookmemokmp.shared.generated.resources.format_franco_belgian_comic
import bookmemokmp.shared.generated.resources.format_light_novel
import bookmemokmp.shared.generated.resources.format_manga
import bookmemokmp.shared.generated.resources.format_manhua
import bookmemokmp.shared.generated.resources.format_manhwa
import bookmemokmp.shared.generated.resources.format_novel
import bookmemokmp.shared.generated.resources.format_one_shot
import bookmemokmp.shared.generated.resources.format_poetry
import bookmemokmp.shared.generated.resources.format_short_story
import bookmemokmp.shared.generated.resources.format_theatre
import bookmemokmp.shared.generated.resources.format_webtoon
import org.jetbrains.compose.resources.StringResource

enum class FormatType(
    val parentType: ItemType,
    val stringRes: StringResource,
) {
    // Literature
    NOVEL(ItemType.LITERATURE, Res.string.format_novel),
    SHORT_STORY(ItemType.LITERATURE, Res.string.format_short_story),
    THEATRE(ItemType.LITERATURE, Res.string.format_theatre),
    BIOGRAPHY(ItemType.LITERATURE, Res.string.format_biography),
    AUTOBIOGRAPHY(ItemType.LITERATURE, Res.string.format_autobiography),
    POETRY(ItemType.LITERATURE, Res.string.format_poetry),
    ESSAY(ItemType.LITERATURE, Res.string.format_essay),
    CHILDRENS(ItemType.LITERATURE, Res.string.format_childrens),

    // Manga
    MANGA(ItemType.MANGA, Res.string.format_manga),
    ONE_SHOT(ItemType.MANGA, Res.string.format_one_shot),
    WEBTOON(ItemType.MANGA, Res.string.format_webtoon),
    MANHWA(ItemType.MANGA, Res.string.format_manhwa),
    MANHUA(ItemType.MANGA, Res.string.format_manhua),
    LIGHT_NOVEL(ItemType.MANGA, Res.string.format_light_novel),
    ANIME(ItemType.MANGA, Res.string.format_anime),

    // Comic
    FRANCO_BELGIAN_COMIC(ItemType.COMIC, Res.string.format_franco_belgian_comic),
    AMERICAN_COMICS(ItemType.COMIC, Res.string.format_american_comics),
    ;

    companion object {
        fun fromString(value: String?): FormatType? = entries.firstOrNull { it.name == value }

        fun forType(type: ItemType): List<FormatType> = entries.filter { it.parentType == type }
    }
}
