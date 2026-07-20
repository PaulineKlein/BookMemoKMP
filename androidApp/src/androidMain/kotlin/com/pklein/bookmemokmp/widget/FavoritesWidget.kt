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
import androidx.glance.color.ColorProvider
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
import com.pklein.bookmemokmp.MainActivity
import com.pklein.bookmemokmp.R
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.repository.ICollectionRepository
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

// Light / dark color pairs for the widget
private val widgetBackground = ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFF1C1C1E))
private val widgetOnSurface = ColorProvider(day = Color(0xFF000000), night = Color(0xFFFFFFFF))
private val widgetSubtle = ColorProvider(day = Color(0xFF757575), night = Color(0xFF9E9E9E))
private val widgetDivider = ColorProvider(day = Color(0xFF9AB8CC), night = Color(0xFF3A5A70))

class FavoritesWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: android.content.Context,
        id: GlanceId,
    ) {
        val repository = GlobalContext.get().get<ICollectionRepository>()
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
                .background(widgetBackground)
                .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = LocalContext.current.getString(R.string.widget_favorites_title),
            style =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = widgetOnSurface,
                ),
            modifier = GlanceModifier.padding(bottom = 6.dp),
        )

        Box(
            modifier =
                GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(widgetDivider),
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
                            color = widgetSubtle,
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
                            color = widgetOnSurface,
                        ),
                    maxLines = 1,
                )
                if (progressParts.isNotEmpty()) {
                    Text(
                        text = progressParts.joinToString(" · "),
                        style =
                            TextStyle(
                                fontSize = 11.sp,
                                color = widgetSubtle,
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
private fun FavoritesContentPreview(
    favorites: List<CollectionItem>,
    dark: Boolean = false,
) {
    val bg = if (dark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    val onSurface = if (dark) Color(0xFFFFFFFF) else Color(0xFF000000)
    val subtle = if (dark) Color(0xFF9E9E9E) else Color(0xFF757575)
    ComposeColumn(
        modifier =
            Modifier
                .composeFillMaxSize()
                .composeBackground(bg)
                .composePadding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        ComposeText(
            text = "Favorites",
            style =
                ComposeTextStyle(
                    fontWeight = ComposeFontWeight.Bold,
                    fontSize = 14.sp,
                    color = onSurface,
                ),
            modifier = Modifier.composePadding(bottom = 6.dp),
        )
        HorizontalDivider(
            color = if (dark) Color(0xFF3A5A70) else Color(0xFF9AB8CC),
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
                    style = ComposeTextStyle(fontSize = 12.sp, color = subtle),
                )
            }
        } else {
            ComposeColumn(modifier = Modifier.composeFillMaxSize()) {
                for (item in favorites) {
                    FavoriteRowPreview(item, onSurface = onSurface, subtle = subtle)
                }
            }
        }
    }
}

@Composable
private fun FavoriteRowPreview(
    item: CollectionItem,
    onSurface: Color = Color.Black,
    subtle: Color = Color(0xFF757575),
) {
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
                style = ComposeTextStyle(fontSize = 13.sp, color = onSurface),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (progressParts.isNotEmpty()) {
                ComposeText(
                    text = progressParts.joinToString(" · "),
                    style = ComposeTextStyle(fontSize = 11.sp, color = subtle),
                )
            }
        }
    }
}

@Preview(name = "Widget — empty (light)", showBackground = true, widthDp = 200, heightDp = 160)
@Composable
private fun PreviewFavoritesEmpty() {
    FavoritesContentPreview(favorites = emptyList(), dark = false)
}

@Preview(
    name = "Widget — empty (dark)",
    showBackground = true,
    backgroundColor = 0xFF1C1C1E,
    widthDp = 200,
    heightDp = 160,
)
@Composable
private fun PreviewFavoritesEmptyDark() {
    FavoritesContentPreview(favorites = emptyList(), dark = true)
}

@Preview(name = "Widget — with items (light)", showBackground = true, widthDp = 200, heightDp = 300)
@Composable
private fun PreviewFavoritesWithItems() {
    FavoritesContentPreview(favorites = previewItems, dark = false)
}

@Preview(
    name = "Widget — with items (dark)",
    showBackground = true,
    backgroundColor = 0xFF1C1C1E,
    widthDp = 200,
    heightDp = 300,
)
@Composable
private fun PreviewFavoritesWithItemsDark() {
    FavoritesContentPreview(favorites = previewItems, dark = true)
}

private val previewItems =
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
    )
