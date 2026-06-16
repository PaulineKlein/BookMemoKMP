package com.pklein.bookmemokmp.presentation.collection.collectionList

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.add_number
import bookmemokmp.shared.generated.resources.bought
import bookmemokmp.shared.generated.resources.check_update
import bookmemokmp.shared.generated.resources.down_accessibility
import bookmemokmp.shared.generated.resources.edit_item
import bookmemokmp.shared.generated.resources.fav_add_accessibility
import bookmemokmp.shared.generated.resources.fav_remove_accessibility
import bookmemokmp.shared.generated.resources.finished
import bookmemokmp.shared.generated.resources.up_accessibility
import bookmemokmp.shared.generated.resources.wishlist
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.JikanType
import com.pklein.bookmemokmp.presentation.collection.cover.CoverSmallPreviewItem
import com.pklein.bookmemokmp.presentation.collection.viewmodel.UpdateCheckState
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import com.pklein.bookmemokmp.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookItem(
    item: CollectionItem,
    onFavoriteToggle: (CollectionItem) -> Unit,
    onEditClick: (CollectionItem) -> Unit,
    onProgressUpdate: (CollectionItem) -> Unit,
    updateCheckState: UpdateCheckState = UpdateCheckState.Idle,
    onCheckForUpdates: (CollectionItem) -> Unit = {},
    onDismissUpdateCheck: () -> Unit = {},
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false,
) {
    var expanded by remember { mutableStateOf(initialExpanded) }
    var showProgressDialog by remember { mutableStateOf(false) }

    if (showProgressDialog) {
        ProgressDialog(
            item = item,
            onDismiss = { showProgressDialog = false },
            onConfirm = { updated ->
                onProgressUpdate(updated)
                showProgressDialog = false
            },
        )
    }
    if (
        updateCheckState !is UpdateCheckState.Loading &&
        updateCheckState !is UpdateCheckState.Idle
    ) {
        UpdateCheckResultDialog(
            item = item,
            state = updateCheckState,
            onDismiss = onDismissUpdateCheck,
        )
    }

    Card(
        onClick = { expanded = !expanded },
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .animateContentSize(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // ── Header (always visible) ───────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                CoverSmallPreviewItem(item.imageUrl)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                TypeBadge(item.type)
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription =
                        if (expanded) {
                            stringResource(Res.string.up_accessibility)
                        } else {
                            stringResource(Res.string.down_accessibility)
                        },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // ── Expanded content ─────────────────────────────────────────────
            if (expanded) {
                Spacer(Modifier.height(8.dp))

                val authorIllustrator =
                    listOfNotNull(
                        item.author?.takeIf { it.isNotBlank() },
                        item.illustrator?.takeIf { it.isNotBlank() },
                    ).takeIf { it.isNotEmpty() }?.joinToString(" & ")

                authorIllustrator?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (((item.year ?: 0) > 0)) {
                    Text(
                        item.year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (item.favorite || item.finished || item.bought) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (item.finished) {
                            Text(
                                text = stringResource(Res.string.finished),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                            )
                        }
                        if (item.bought) {
                            Text(
                                text = stringResource(Res.string.bought),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                        if (item.wishlist) {
                            Text(
                                text = stringResource(Res.string.wishlist),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                ProgressCard(item)
                Spacer(Modifier.height(6.dp))

                if (!item.notes.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        item.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (!item.description.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(6.dp))

                if (item.isBorrowed) {
                    Spacer(Modifier.height(4.dp))
                    LoanCard(
                        borrowedBy = item.borrowedBy,
                        borrowedSince = item.borrowedSince,
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                )

                // ── Action row ────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { showProgressDialog = true }) {
                        Text(
                            stringResource(Res.string.add_number),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    IconButton(onClick = { onEditClick(item) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(Res.string.edit_item),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    if (item.jikanId != null) {
                        if (updateCheckState is UpdateCheckState.Loading) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier
                                        .padding(horizontal = 12.dp)
                                        .width(20.dp)
                                        .height(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        } else {
                            IconButton(onClick = { onCheckForUpdates(item) }) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = stringResource(Res.string.check_update),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                    }
                    IconButton(onClick = { onFavoriteToggle(item.copy(favorite = !item.favorite)) }) {
                        Icon(
                            imageVector = if (item.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription =
                                if (item.favorite) {
                                    stringResource(Res.string.fav_add_accessibility)
                                } else {
                                    stringResource(Res.string.fav_remove_accessibility)
                                },
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

/** Collapsed card — default state on list. */
@Preview
@Composable
private fun PreviewBookItemCollapsed() {
    BookMemoTheme {
        BookItem(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.LITERATURE,
                    title = "The Lord of the Rings",
                    author = "J.R.R. Tolkien",
                    imageUrl = "https://covers.openlibrary.org/b/id/8231996-L.jpg",
                ),
            onFavoriteToggle = {},
            onEditClick = {},
            onProgressUpdate = {},
        )
    }
}

/** Minimal comic — title only. */
@Preview
@Composable
private fun PreviewBookItemMinimal() {
    BookMemoTheme {
        BookItem(
            item =
                CollectionItem(
                    id = 3,
                    type = ItemType.COMIC,
                    title = "Tintin au Tibet",
                ),
            onFavoriteToggle = {},
            onEditClick = {},
            onProgressUpdate = {},
        )
    }
}

/** Expanded book — author, description, progress, favorite, action row. */
@Preview
@Composable
private fun PreviewBookItemExpanded() {
    BookMemoTheme {
        BookItem(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.LITERATURE,
                    title = "The Lord of the Rings",
                    author = "J.R.R. Tolkien",
                    illustrator = "Toto",
                    year = 1954,
                    description = "An epic high-fantasy novel following the quest to destroy the One Ring.",
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
            onFavoriteToggle = {},
            onEditClick = {},
            onProgressUpdate = {},
            initialExpanded = true,
        )
    }
}

/** Expanded book — author, description, progress, favorite, action row. */
@Preview(fontScale = 2.0f)
@Composable
private fun PreviewBookItemExpandedBigFont() {
    BookMemoTheme {
        BookItem(
            item =
                CollectionItem(
                    id = 1,
                    type = ItemType.LITERATURE,
                    title = "The Lord of the Rings",
                    author = "J.R.R. Tolkien",
                    year = 1954,
                    description = "An epic high-fantasy novel following the quest to destroy the One Ring.",
                    bought = true,
                    favorite = true,
                    finished = true,
                    isBorrowed = true,
                    borrowedBy = "Jean-Baptiste",
                    borrowedSince = 1_746_057_600_000L, // 2025-05-01
                    tome = 3,
                    chapter = 42,
                    season = 1,
                    episode = 5,
                ),
            onFavoriteToggle = {},
            onEditClick = {},
            onProgressUpdate = {},
            initialExpanded = true,
        )
    }
}

/** Expanded manga — all progress fields, action row. */
@Preview
@Composable
private fun PreviewBookItemMangaExpanded() {
    BookMemoTheme {
        BookItem(
            item =
                CollectionItem(
                    id = 2,
                    type = ItemType.MANGA,
                    title = "One Piece",
                    author = "Eiichiro Oda",
                    bought = true,
                    tome = 107,
                    chapter = 1100,
                    episode = 1090,
                    totTome = 200,
                    totChapter = 1200,
                    totEpisode = 2000,
                    jikanId = 21_000L,
                    jikanType = JikanType.MANGA,
                ),
            onFavoriteToggle = {},
            onEditClick = {},
            onProgressUpdate = {},
            initialExpanded = true,
        )
    }
}
