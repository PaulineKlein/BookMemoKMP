package com.pklein.bookmemokmp.presentation.collection.statistics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.progress_section
import bookmemokmp.shared.generated.resources.statistics_book
import bookmemokmp.shared.generated.resources.statistics_chapter
import bookmemokmp.shared.generated.resources.statistics_comics
import bookmemokmp.shared.generated.resources.statistics_episode
import bookmemokmp.shared.generated.resources.statistics_manga
import bookmemokmp.shared.generated.resources.statistics_type
import bookmemokmp.shared.generated.resources.statistics_volume
import bookmemokmp.shared.generated.resources.stats_total
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.ui.theme.BadgeBookColor
import com.pklein.bookmemokmp.ui.theme.BadgeComicColor
import com.pklein.bookmemokmp.ui.theme.BadgeMangaColor
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatisticsScreen(
    allItems: List<CollectionItem>,
    filteredItems: List<CollectionItem>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val globalStats = remember(allItems, filteredItems) {
        ItemType.entries.map { type ->
            Triple(
                type,
                allItems.count { it.type == type },
                filteredItems.count { it.type == type }
            )
        }
    }

    val progressStats = remember(allItems, filteredItems) {
        listOf(
            Triple(
                Res.string.statistics_volume,
                allItems.sumOf { it.tome ?: 0 },
                filteredItems.sumOf { it.tome ?: 0 }
            ) to Color(0xFF7ABEFB),
            Triple(
                Res.string.statistics_chapter,
                allItems.sumOf { it.chapter ?: 0 },
                filteredItems.sumOf { it.chapter ?: 0 }
            ) to Color(0xFF6BA3D0),
            Triple(
                Res.string.statistics_episode,
                allItems.sumOf { it.episode ?: 0 },
                filteredItems.sumOf { it.episode ?: 0 }
            ) to Color(0xFFF5685AE)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .padding(bottom = 72.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(Res.string.statistics_type),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        globalStats.forEach { (type, total, filtered) ->
            val label = when (type) {
                ItemType.LITERATURE -> stringResource(Res.string.statistics_book)
                ItemType.MANGA -> stringResource(Res.string.statistics_manga)
                ItemType.COMIC -> stringResource(Res.string.statistics_comics)
            }
            val color = when (type) {
                ItemType.LITERATURE -> BadgeBookColor
                ItemType.MANGA -> BadgeMangaColor
                ItemType.COMIC -> BadgeComicColor
            }
            TypeStatBar(label = label, total = total, filtered = filtered, color = color)
        }

        TypeStatBar(
            label = stringResource(Res.string.stats_total),
            total = allItems.size,
            filtered = filteredItems.size,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = stringResource(Res.string.progress_section),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )
        progressStats.forEach { (stat, color) ->
            val (label, total, filtered) = stat
            TypeStatBar(
                label = stringResource(label),
                total = total,
                filtered = filtered,
                color = color
            )
        }
    }
}


// ── Type stat bar ─────────────────────────────────────────────────────────────

@Composable
private fun TypeStatBar(
    label: String,
    total: Int,
    filtered: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val fraction by animateFloatAsState(
        targetValue = if (total > 0) filtered.toFloat() / total else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "bar_$label"
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$filtered / $total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewAllItems = listOf(
    CollectionItem(
        id = 1,
        type = ItemType.LITERATURE,
        title = "The Lord of the Rings",
        author = "J.R.R. Tolkien",
        bought = true,
        favorite = true
    ),
    CollectionItem(
        id = 2,
        type = ItemType.LITERATURE,
        title = "Dune",
        author = "Frank Herbert",
        bought = true
    ),
    CollectionItem(
        id = 3,
        type = ItemType.MANGA,
        title = "One Piece",
        author = "Eiichiro Oda",
        bought = true,
        tome = 107
    ),
    CollectionItem(
        id = 4,
        type = ItemType.MANGA,
        title = "Naruto",
        author = "Masashi Kishimoto",
        finished = true
    ),
    CollectionItem(
        id = 5,
        type = ItemType.MANGA,
        title = "Berserk",
        author = "Kentaro Miura",
        bought = true
    ),
    CollectionItem(
        id = 6,
        type = ItemType.COMIC,
        title = "Tintin au Tibet",
        author = "Hergé",
        finished = true
    ),
)

private val previewFilteredItems = listOf(
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
            scrollState = ScrollState(0)
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
            scrollState = ScrollState(0)
        )
    }
}
