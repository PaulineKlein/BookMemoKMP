package com.pklein.bookmemokmp.presentation.collection.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.statistics_book
import bookmemokmp.shared.generated.resources.statistics_chapter
import bookmemokmp.shared.generated.resources.statistics_comics
import bookmemokmp.shared.generated.resources.statistics_episode
import bookmemokmp.shared.generated.resources.statistics_manga
import bookmemokmp.shared.generated.resources.statistics_of_total
import bookmemokmp.shared.generated.resources.statistics_progress
import bookmemokmp.shared.generated.resources.statistics_type
import bookmemokmp.shared.generated.resources.statistics_volume
import bookmemokmp.shared.generated.resources.statistics_watch_time
import bookmemokmp.shared.generated.resources.statistics_watch_time_days
import bookmemokmp.shared.generated.resources.statistics_watch_time_years
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import com.pklein.bookmemokmp.ui.theme.typeColors
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatisticsScreen(
    allItems: List<CollectionItem>,
    filteredItems: List<CollectionItem>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    val globalStats =
        remember(allItems, filteredItems) {
            ItemType.entries.map { type ->
                Triple(
                    type,
                    allItems.count { it.type == type },
                    filteredItems.count { it.type == type },
                )
            }
        }

    val watchTimeEpisodes =
        remember(filteredItems) { filteredItems.sumOf { it.episode ?: 0 } }

    val progressStats =
        remember(allItems, filteredItems) {
            listOf(
                Triple(
                    Res.string.statistics_volume,
                    allItems.sumOf { it.tome ?: 0 },
                    filteredItems.sumOf { it.tome ?: 0 },
                ),
                Triple(
                    Res.string.statistics_chapter,
                    allItems.sumOf { it.chapter ?: 0 },
                    filteredItems.sumOf { it.chapter ?: 0 },
                ),
                Triple(
                    Res.string.statistics_episode,
                    allItems.sumOf { it.episode ?: 0 },
                    filteredItems.sumOf { it.episode ?: 0 },
                ),
            )
        }

    val isScoped = filteredItems.size != allItems.size
    val colors = typeColors()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(bottom = 72.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SectionTitle(stringResource(Res.string.statistics_type))

        ItemsCard(
            shown = filteredItems.size,
            total = allItems.size,
            isScoped = isScoped,
            sections =
                globalStats.map { (type, total, shown) ->
                    SectionStat(
                        label =
                            when (type) {
                                ItemType.LITERATURE -> stringResource(Res.string.statistics_book)
                                ItemType.MANGA -> stringResource(Res.string.statistics_manga)
                                ItemType.COMIC -> stringResource(Res.string.statistics_comics)
                            },
                        color =
                            when (type) {
                                ItemType.LITERATURE -> colors.book
                                ItemType.MANGA -> colors.manga
                                ItemType.COMIC -> colors.comic
                            },
                        shown = shown,
                        total = total,
                    )
                },
        )

        SectionTitle(
            text = stringResource(Res.string.statistics_progress),
            modifier = Modifier.padding(top = 8.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            progressStats.take(2).forEach { (label, total, shown) ->
                StatTile(
                    label = stringResource(label),
                    value = shown,
                    total = total,
                    showTotal = isScoped,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            progressStats.drop(2).forEach { (label, total, shown) ->
                StatTile(
                    label = stringResource(label),
                    value = shown,
                    total = total,
                    showTotal = isScoped,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
            StatTile(
                label = stringResource(Res.string.statistics_watch_time),
                value = watchTimeEpisodes,
                formatValue = { formatWatchTime(it) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun formatWatchTime(episodes: Int): String {
    val totalMinutes = episodes * 25
    val totalDays = totalMinutes / (24 * 60)
    val years = totalDays / 365
    val days = totalDays % 365
    val hours = totalMinutes % (24 * 60) / 60
    val minutes = totalMinutes % 60
    val parts =
        buildList {
            if (years > 0) {
                add(
                    pluralStringResource(
                        Res.plurals.statistics_watch_time_years,
                        years,
                        years,
                    ),
                )
            }
            if (days > 0) {
                add(
                    pluralStringResource(
                        Res.plurals.statistics_watch_time_days,
                        days,
                        days,
                    ),
                )
            }
            if (hours > 0) add("${hours}h")
            if (minutes > 0) add("${minutes}min")
        }
    return parts.joinToString(" ").ifEmpty { "0min" }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

// The card fill is nearly identical to the screen background, so the 1dp outline carries the card edge.
@Composable
private fun StatsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        content = content,
    )
}

// ── Animation ─────────────────────────────────────────────────────────────────

private const val StatsAnimationMillis = 600
private const val MinSegmentWeight = 0.0001f

@Composable
private fun animatedCount(target: Int): Int =
    animateIntAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = StatsAnimationMillis),
        label = "count",
    ).value

// ── Items card ────────────────────────────────────────────────────────────────

private data class SectionStat(
    val label: String,
    val color: Color,
    val shown: Int,
    val total: Int,
)

@Composable
private fun ItemsCard(
    shown: Int,
    total: Int,
    isScoped: Boolean,
    sections: List<SectionStat>,
    modifier: Modifier = Modifier,
) {
    StatsCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = animatedCount(shown).toString(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                AnimatedVisibility(visible = isScoped) {
                    Text(
                        text = stringResource(Res.string.statistics_of_total, total),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
                    )
                }
            }

            CompositionBar(sections = sections)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sections.forEach { section ->
                    SectionRow(section = section, isScoped = isScoped)
                }
            }
        }
    }
}

@Composable
private fun CompositionBar(sections: List<SectionStat>) {
    val shownTotal = sections.sumOf { it.shown }
    val fractions =
        sections.map { section ->
            animateFloatAsState(
                targetValue = if (shownTotal > 0) section.shown.toFloat() / shownTotal else 0f,
                animationSpec = tween(durationMillis = StatsAnimationMillis),
                label = "segment_${section.label}",
            ).value
        }
    // Neutral filler so an empty result shows a plain track instead of three equal colored segments.
    val emptyFraction =
        animateFloatAsState(
            targetValue = if (shownTotal == 0) 1f else 0f,
            animationSpec = tween(durationMillis = StatsAnimationMillis),
            label = "segment_empty",
        ).value

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .clearAndSetSemantics {},
    ) {
        sections.forEachIndexed { index, section ->
            BarSegment(
                weight = fractions[index],
                color = section.color,
            )
        }
        BarSegment(
            weight = emptyFraction,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
private fun RowScope.BarSegment(
    weight: Float,
    color: Color,
) {
    Box(
        modifier =
            Modifier
                .weight(weight.coerceAtLeast(MinSegmentWeight))
                .fillMaxHeight()
                .background(color),
    )
}

@Composable
private fun SectionRow(
    section: SectionStat,
    isScoped: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(10.dp)
                    .background(section.color, CircleShape),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = section.label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = animatedCount(section.shown).toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        AnimatedVisibility(visible = isScoped) {
            Text(
                text = " " + stringResource(Res.string.statistics_of_total, section.total),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Stat tile ─────────────────────────────────────────────────────────────────

@Composable
private fun StatTile(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    total: Int = 0,
    showTotal: Boolean = false,
    formatValue: @Composable (Int) -> String = { it.toString() },
) {
    StatsCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatValue(animatedCount(value)),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            AnimatedVisibility(visible = showTotal) {
                Text(
                    text = stringResource(Res.string.statistics_of_total, total),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewAllItems =
    listOf(
        CollectionItem(
            id = 1,
            type = ItemType.LITERATURE,
            title = "The Lord of the Rings",
            author = "J.R.R. Tolkien",
            bought = true,
            favorite = true,
        ),
        CollectionItem(
            id = 2,
            type = ItemType.LITERATURE,
            title = "Dune",
            author = "Frank Herbert",
            bought = true,
        ),
        CollectionItem(
            id = 3,
            type = ItemType.MANGA,
            title = "One Piece",
            author = "Eiichiro Oda",
            episode = 100,
            bought = true,
            tome = 107,
        ),
        CollectionItem(
            id = 4,
            type = ItemType.MANGA,
            title = "Naruto",
            author = "Masashi Kishimoto",
            finished = true,
        ),
        CollectionItem(
            id = 5,
            type = ItemType.MANGA,
            title = "Berserk",
            author = "Kentaro Miura",
            bought = true,
        ),
        CollectionItem(
            id = 6,
            type = ItemType.COMIC,
            title = "Tintin au Tibet",
            author = "Hergé",
            finished = true,
        ),
    )

private val previewFilteredItems =
    listOf(
        previewAllItems[0],
        previewAllItems[2],
        previewAllItems[3],
        previewAllItems[5],
    )

@Preview(showBackground = true)
@Composable
private fun PreviewStatistics() {
    BookMemoTheme {
        StatisticsScreen(
            allItems = previewAllItems,
            filteredItems = previewFilteredItems,
            scrollState = ScrollState(0),
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewStatisticsBigFont() {
    BookMemoTheme {
        StatisticsScreen(
            allItems = previewAllItems,
            filteredItems = previewFilteredItems,
            scrollState = ScrollState(0),
        )
    }
}

/** No filter: the whole collection. */
@Preview(showBackground = true)
@Composable
private fun PreviewStatisticsWholeCollection() {
    BookMemoTheme {
        StatisticsScreen(
            allItems = previewAllItems,
            filteredItems = previewAllItems,
            scrollState = ScrollState(0),
        )
    }
}

/** Filtered subset — dark theme, big font. */
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewStatisticsFilteredDark() {
    BookMemoTheme {
        StatisticsScreen(
            allItems = previewAllItems,
            filteredItems = listOf(previewAllItems[2]),
            scrollState = ScrollState(0),
        )
    }
}
