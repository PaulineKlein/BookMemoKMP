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
import bookmemokmp.shared.generated.resources.volumes_missing
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProgressCard(item: CollectionItem) {
    val hasVolume = (item.tome ?: 0) > 0 || (item.chapter ?: 0) > 0
    val hasEpisode = (item.season ?: 0) > 0 || (item.episode ?: 0) > 0
    val hasCheckedTomes = item.checkedTomes.isNotEmpty()
    val missingLabel = item.missingTomesLabel()
    if (!hasVolume && !hasEpisode && !hasCheckedTomes && missingLabel == null) return

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
                if ((item.tome ?: 0) > 0) {
                    if ((item.totTome ?: 0) > 0) {
                        stringResource(Res.string.vol_number, item.tome ?: 0) +
                            " / " +
                            item.totTome
                    } else {
                        stringResource(Res.string.vol_number, item.tome ?: 0)
                    }
                } else {
                    null
                }

            val chapterItem =
                if ((item.chapter ?: 0) > 0) {
                    if ((item.totChapter ?: 0) > 0) {
                        stringResource(Res.string.chap_number, item.chapter ?: 0) +
                            " / " +
                            item.totChapter
                    } else {
                        stringResource(Res.string.chap_number, item.chapter ?: 0)
                    }
                } else {
                    null
                }

            val episodeItem =
                if ((item.episode ?: 0) > 0) {
                    if ((item.totEpisode ?: 0) > 0) {
                        stringResource(Res.string.ep_number, item.episode ?: 0) +
                            " / " +
                            item.totEpisode
                    } else {
                        stringResource(Res.string.ep_number, item.episode ?: 0)
                    }
                } else {
                    null
                }

            val seasonItem =
                if ((item.season ?: 0) > 0) {
                    stringResource(Res.string.season_number, item.season ?: 0)
                } else {
                    null
                }

            if (hasVolume) {
                Text(
                    text = listOfNotNull(tomeItem, chapterItem).joinToString("  ·  "),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = onContainer,
                )
            }
            if (hasEpisode) {
                Text(
                    text = listOfNotNull(seasonItem, episodeItem).joinToString("  ·  "),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = onContainer,
                )
            }
            if (missingLabel != null) {
                Text(
                    text = stringResource(Res.string.volumes_missing, missingLabel),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
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
                    borrowedSince = 1_746_057_600_000L,
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
                    borrowedSince = 1_746_057_600_000L,
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
                    borrowedSince = 1_746_057_600_000L,
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

@Preview(showBackground = true)
@Composable
private fun ProgressCardWithMissingPreview() {
    BookMemoTheme {
        ProgressCard(
            item =
                CollectionItem(
                    id = 2,
                    type = ItemType.MANGA,
                    title = "One Piece",
                    author = "Eiichiro Oda",
                    tome = 10,
                    totTome = 10,
                    checkedTomes = listOf(1, 2, 3, 5, 8, 9, 10),
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
                    borrowedSince = 1_746_057_600_000L,
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
                    borrowedSince = 1_746_057_600_000L,
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
