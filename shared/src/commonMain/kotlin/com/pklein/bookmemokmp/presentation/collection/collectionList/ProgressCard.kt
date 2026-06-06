package com.pklein.bookmemokmp.presentation.collection.collectionList

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.chap_number
import bookmemokmp.shared.generated.resources.ep_number
import bookmemokmp.shared.generated.resources.season_number
import bookmemokmp.shared.generated.resources.vol_number
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProgressCard(item: CollectionItem) {
    val hasVolume = item.tome != null || item.chapter != null
    val hasEpisode = item.season != null || item.episode != null
    if (!hasVolume && !hasEpisode) return

    val onContainer = MaterialTheme.colorScheme.onPrimaryContainer

    Row(
        modifier =
            Modifier
                .border(1.dp, onContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val tomeItem =
                item.tome?.let {
                    if ((item.totTome ?: 0) > 0) {
                        stringResource(Res.string.vol_number, it) + " / " + item.totTome
                    } else {
                        stringResource(Res.string.vol_number, it)
                    }
                }
            val chapterItem =
                item.chapter?.let {
                    if ((item.totChapter ?: 0) > 0) {
                        stringResource(Res.string.chap_number, it) + " / " + item.totChapter
                    } else {
                        stringResource(Res.string.chap_number, it)
                    }
                }
            val volumeLine = listOfNotNull(tomeItem, chapterItem)
            val episodeItem =
                item.episode?.let {
                    if ((item.totEpisode ?: 0) > 0) {
                        stringResource(Res.string.ep_number, it) + " / " + item.totEpisode
                    } else {
                        stringResource(Res.string.ep_number, it)
                    }
                }
            val seasonItem =
                item.season?.let {
                    stringResource(Res.string.season_number, it)
                }

            val episodeLine = listOfNotNull(seasonItem, episodeItem)

            if (hasVolume) {
                Text(
                    text = volumeLine.joinToString("  ·  "),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            if (hasEpisode) {
                Text(
                    text = episodeLine.joinToString("  ·  "),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ProgressCardPreview() {
    BookMemoTheme {
        ProgressCard(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.LITERATURE,
                    title = "The Lord of the Rings",
                    author = "J.R.R. Tolkien",
                    year = 1954,
                    description = "",
                    bought = true,
                    favorite = true,
                    finished = true,
                    isBorrowed = true,
                    borrowedBy = "Alice",
                    borrowedSince = 1_746_057_600_000L, // 2025-05-01
                    tome = 3,
                    chapter = 42,
                    season = 1,
                    episode = 5,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressCardNoVolumeNoEpisodePreview() {
    BookMemoTheme {
        ProgressCard(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.LITERATURE,
                    title = "The Lord of the Rings",
                    author = "J.R.R. Tolkien",
                    year = 1954,
                    description = "",
                    bought = true,
                    favorite = true,
                    finished = true,
                    isBorrowed = true,
                    borrowedBy = "Alice",
                    borrowedSince = 1_746_057_600_000L, // 2025-05-01
                    tome = null,
                    totTome = 10,
                    chapter = 42,
                    totChapter = 0,
                    season = 1,
                    episode = null,
                    totEpisode = 10,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressCardFullPreview() {
    BookMemoTheme {
        ProgressCard(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.LITERATURE,
                    title = "The Lord of the Rings",
                    author = "J.R.R. Tolkien",
                    year = 1954,
                    description = "",
                    bought = true,
                    favorite = true,
                    finished = true,
                    isBorrowed = true,
                    borrowedBy = "Alice",
                    borrowedSince = 1_746_057_600_000L, // 2025-05-01
                    tome = 3,
                    chapter = 42,
                    season = 1,
                    episode = 5,
                    totEpisode = 10,
                    totTome = 5,
                    totChapter = 100,
                ),
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun ProgressCardPreviewBigFont() {
    BookMemoTheme {
        ProgressCard(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.LITERATURE,
                    title = "The Lord of the Rings",
                    author = "J.R.R. Tolkien",
                    year = 1954,
                    description = "",
                    bought = true,
                    favorite = true,
                    finished = true,
                    isBorrowed = true,
                    borrowedBy = "Alice",
                    borrowedSince = 1_746_057_600_000L, // 2025-05-01
                    tome = 3,
                    chapter = 42,
                    season = 1,
                    episode = 5,
                ),
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun ProgressCardFullPreviewBigFont() {
    BookMemoTheme {
        ProgressCard(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.LITERATURE,
                    title = "The Lord of the Rings",
                    author = "J.R.R. Tolkien",
                    year = 1954,
                    description = "",
                    bought = true,
                    favorite = true,
                    finished = true,
                    isBorrowed = true,
                    borrowedBy = "Alice",
                    borrowedSince = 1_746_057_600_000L, // 2025-05-01
                    tome = 3,
                    chapter = 42,
                    season = 1,
                    episode = 5,
                    totEpisode = 10,
                    totTome = 5,
                    totChapter = 100,
                ),
        )
    }
}
