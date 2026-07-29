package com.pklein.bookmemokmp.presentation.collection.discover

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import bookmemokmp.shared.generated.resources.discover_add_wishlist
import bookmemokmp.shared.generated.resources.discover_added
import bookmemokmp.shared.generated.resources.discover_description_english_only
import bookmemokmp.shared.generated.resources.down_accessibility
import bookmemokmp.shared.generated.resources.up_accessibility
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.presentation.collection.cover.CoverSmallPreviewItem
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

private enum class WishlistButtonState { IDLE, LOADING, ADDED }

@Composable
fun DiscoverBookItem(
    result: SearchResult,
    saveEnglishDescription: Boolean = true,
    onAddToWishlist: () -> Unit,
    initialExpanded: Boolean = false,
) {
    var expanded by remember { mutableStateOf(initialExpanded) }
    var wishlistState by remember { mutableStateOf(WishlistButtonState.IDLE) }

    LaunchedEffect(wishlistState) {
        if (wishlistState == WishlistButtonState.LOADING) {
            onAddToWishlist()
            wishlistState = WishlistButtonState.ADDED
        }
    }

    Card(
        onClick = { expanded = !expanded },
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .animateContentSize(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // ── Header ───────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                CoverSmallPreviewItem(result.imageUrl)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
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

                result.author?.takeIf { it.isNotBlank() }?.let { author ->
                    Text(
                        text = author,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                result.year?.let { year ->
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(6.dp))
                if (saveEnglishDescription) {
                    result.description?.takeIf { it.isNotBlank() }?.let { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(Res.string.discover_description_english_only),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (wishlistState) {
                        WishlistButtonState.IDLE -> {
                            Button(onClick = { wishlistState = WishlistButtonState.LOADING }) {
                                Text(stringResource(Res.string.discover_add_wishlist))
                            }
                        }
                        WishlistButtonState.LOADING -> {
                            Button(onClick = {}, enabled = false) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                        WishlistButtonState.ADDED -> {
                            Text(
                                text = stringResource(Res.string.discover_added),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val sampleFull =
    SearchResult(
        title = "Berserk",
        author = "Kentaro Miura",
        year = 1989,
        description = "A dark fantasy manga following Guts, a lone mercenary warrior on a journey to find his destiny.",
    )

private val sampleNoDescription =
    SearchResult(
        title = "A Silent Voice",
        author = "Yoshitoki Oima",
        year = 2013,
        description = null,
    )

private val sampleLongTitle =
    SearchResult(
        title = "Fullmetal Alchemist: The Sacred Star of Milos — Special Edition Volume",
        author = "Hiromu Arakawa",
        year = 2001,
        description = "Two brothers search for a Philosopher's Stone after an attempt to revive their deceased mother goes wrong.",
    )

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverBookItemCollapsed() {
    BookMemoTheme {
        DiscoverBookItem(result = sampleFull, onAddToWishlist = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverBookItemExpanded() {
    BookMemoTheme {
        DiscoverBookItem(result = sampleFull, onAddToWishlist = {}, initialExpanded = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverBookItemNoDescription() {
    BookMemoTheme {
        DiscoverBookItem(result = sampleNoDescription, onAddToWishlist = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDiscoverBookItemLongTitle() {
    BookMemoTheme {
        DiscoverBookItem(result = sampleLongTitle, onAddToWishlist = {})
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewDiscoverBookItemBigFont() {
    BookMemoTheme {
        DiscoverBookItem(result = sampleFull, onAddToWishlist = {})
    }
}
