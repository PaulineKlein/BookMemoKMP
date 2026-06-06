package com.pklein.bookmemokmp.widget

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.pklein.bookmemokmp.MainActivity
import com.pklein.bookmemokmp.R
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext
import androidx.compose.foundation.background as composeBackground
import androidx.compose.foundation.layout.Box as ComposeBox
import androidx.compose.foundation.layout.Column as ComposeColumn
import androidx.compose.foundation.layout.Row as ComposeRow
import androidx.compose.foundation.layout.fillMaxSize as composeFillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth as composeFillMaxWidth
import androidx.compose.foundation.layout.padding as composePadding
import androidx.compose.material3.Text as ComposeText
import androidx.compose.ui.Alignment as ComposeAlignment
import androidx.compose.ui.text.TextStyle as ComposeTextStyle
import androidx.compose.ui.text.font.FontWeight as ComposeFontWeight

const val EXTRA_EDIT_ITEM_ID = "EDIT_ITEM_ID"

class FavoritesWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: android.content.Context,
        id: GlanceId,
    ) {
        val repository = GlobalContext.get().get<CollectionRepository>()
        val favorites = repository.getFavorites().first()
        provideContent {
            FavoritesContent(favorites)
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun FavoritesContent(favorites: List<CollectionItem>) {
    Column(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = LocalContext.current.getString(R.string.widget_favorites_title),
            style =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColorProvider(Color.Black),
                ),
            modifier = GlanceModifier.padding(bottom = 6.dp),
        )

        Box(
            modifier =
                GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF9AB8CC)),
        ) {}

        if (favorites.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.widget_empty),
                    style =
                        TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(Color(0xFF757575)),
                        ),
                )
            }
        } else {
            LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                items(favorites) { item ->
                    FavoriteRow(item)
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun FavoriteRow(item: CollectionItem) {
    val context = LocalContext.current
    val intent =
        Intent(context, MainActivity::class.java).apply {
            action = "ACTION_EDIT_ITEM"
            putExtra(EXTRA_EDIT_ITEM_ID, item.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    val progressParts =
        buildList {
            item.tome?.let { add(LocalContext.current.getString(R.string.vol_number, it)) }
            item.chapter?.let { add(LocalContext.current.getString(R.string.chap_number, it)) }
            item.season?.let { add(LocalContext.current.getString(R.string.season_number, it)) }
            item.episode?.let { add(LocalContext.current.getString(R.string.ep_number, it)) }
        }

    Column(modifier = GlanceModifier.fillMaxWidth()) {
        Row(
            modifier =
                GlanceModifier
                    .fillMaxWidth()
                    .clickable(actionStartActivity(intent))
                    .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.type.toEmoji(),
                style = TextStyle(fontSize = 14.sp),
                modifier = GlanceModifier.padding(end = 8.dp),
            )
            Column(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    text = item.title,
                    style =
                        TextStyle(
                            fontSize = 13.sp,
                            color = ColorProvider(Color.Black),
                        ),
                    maxLines = 1,
                )
                if (progressParts.isNotEmpty()) {
                    Text(
                        text = progressParts.joinToString(" · "),
                        style =
                            TextStyle(
                                fontSize = 11.sp,
                                color = ColorProvider(Color(0xFF757575)),
                            ),
                    )
                }
            }
        }
    }
}

private fun ItemType.toEmoji() =
    when (this) {
        ItemType.LITERATURE -> "\uD83D\uDCD8 "

        // 📘 (blue book)
        ItemType.MANGA -> "\uD83C\uDF38 "

        // 🌸 (cherry blossom)
        ItemType.COMIC -> "\uD83D\uDCAC " // 💬 (speech balloon)
    }

// ── Standard-Compose preview mirrors ─────────────────────────────────────────
// Glance composables cannot be rendered by @Preview; these wrappers replicate
// the widget's visual structure using standard Compose for the IDE preview pane.

@Composable
private fun FavoritesContentPreview(favorites: List<CollectionItem>) {
    ComposeColumn(
        modifier =
            Modifier
                .composeFillMaxSize()
                .composeBackground(Color.White)
                .composePadding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        ComposeText(
            text = "Favorites",
            style =
                ComposeTextStyle(
                    fontWeight = ComposeFontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black,
                ),
            modifier = Modifier.composePadding(bottom = 6.dp),
        )
        HorizontalDivider(
            color = Color(0xFF9AB8CC),
            thickness = 1.dp,
            modifier = Modifier.composePadding(bottom = 6.dp),
        )
        if (favorites.isEmpty()) {
            ComposeBox(
                modifier = Modifier.composeFillMaxSize(),
                contentAlignment = ComposeAlignment.Center,
            ) {
                ComposeText(
                    text = "No favorites yet",
                    style =
                        ComposeTextStyle(
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                        ),
                )
            }
        } else {
            ComposeColumn(modifier = Modifier.composeFillMaxSize()) {
                for (item in favorites) {
                    FavoriteRowPreview(item)
                }
            }
        }
    }
}

@Composable
private fun FavoriteRowPreview(item: CollectionItem) {
    val progressParts =
        buildList {
            item.tome?.let { add("Vol. $it") }
            item.chapter?.let { add("Ch. $it") }
            item.season?.let { add("S. $it") }
            item.episode?.let { add("Ep. $it") }
        }
    ComposeRow(
        modifier =
            Modifier
                .composeFillMaxWidth()
                .composePadding(vertical = 6.dp),
        verticalAlignment = ComposeAlignment.CenterVertically,
    ) {
        ComposeText(
            text = item.type.toEmoji(),
            style = ComposeTextStyle(fontSize = 14.sp),
            modifier = Modifier.composePadding(end = 8.dp),
        )
        ComposeColumn(modifier = Modifier.composeFillMaxWidth()) {
            ComposeText(
                text = item.title,
                style =
                    ComposeTextStyle(
                        fontSize = 13.sp,
                        color = Color.Black,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (progressParts.isNotEmpty()) {
                ComposeText(
                    text = progressParts.joinToString(" · "),
                    style =
                        ComposeTextStyle(
                            fontSize = 11.sp,
                            color = Color(0xFF757575),
                        ),
                )
            }
        }
    }
}

@Preview(name = "Widget — empty", showBackground = true, widthDp = 200, heightDp = 160)
@Composable
private fun PreviewFavoritesEmpty() {
    FavoritesContentPreview(favorites = emptyList())
}

@Preview(name = "Widget — with items", showBackground = true, widthDp = 200, heightDp = 300)
@Composable
private fun PreviewFavoritesWithItems() {
    FavoritesContentPreview(
        favorites =
            listOf(
                CollectionItem(
                    id = 1,
                    type = ItemType.LITERATURE,
                    title = "The Hobbit",
                    author = "Tolkien",
                    favorite = true,
                ),
                CollectionItem(
                    id = 2,
                    type = ItemType.MANGA,
                    title = "Berserk",
                    tome = 12,
                    chapter = 980,
                    episode = 100,
                    season = 2,
                    favorite = true,
                ),
                CollectionItem(id = 3, type = ItemType.COMIC, title = "Watchmen", favorite = true),
            ),
    )
}
