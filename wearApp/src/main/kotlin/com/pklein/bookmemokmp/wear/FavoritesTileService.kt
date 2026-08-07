package com.pklein.bookmemokmp.wear

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Box
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.FontStyle
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.LayoutElementBuilders.Row
import androidx.wear.protolayout.LayoutElementBuilders.Spacer
import androidx.wear.protolayout.LayoutElementBuilders.Text
import androidx.wear.protolayout.ModifiersBuilders.Background
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ModifiersBuilders.Modifiers
import androidx.wear.protolayout.ModifiersBuilders.Padding
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.future
import org.koin.android.ext.android.inject

private const val TILE_FRESHNESS_MS = 30 * 60 * 1000L

private const val COLOR_BACKGROUND = 0xFF1C1C1E.toInt()
private const val COLOR_ON_SURFACE = 0xFFFFFFFF.toInt()
private const val COLOR_SUBTITLE = 0xFF9E9E9E.toInt()
private const val COLOR_ACCENT = 0xFF9AB8CC.toInt()

class FavoritesTileService : TileService() {
    private val repository: WearFavoritesRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest) =
        scope.future {
            val favorites = repository.getFavorites().first()
            buildTile(this@FavoritesTileService, favorites)
        }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest) =
        scope.future {
            ResourceBuilders.Resources
                .Builder()
                .setVersion("1")
                .build()
        }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

private fun buildTile(
    context: Context,
    favorites: List<CollectionItem>,
): TileBuilders.Tile =
    TileBuilders.Tile
        .Builder()
        .setResourcesVersion("1")
        .setFreshnessIntervalMillis(TILE_FRESHNESS_MS)
        .setTileTimeline(
            TimelineBuilders.Timeline.fromLayoutElement(
                buildLayout(context, favorites),
            ),
        ).build()

private fun buildLayout(
    context: Context,
    favorites: List<CollectionItem>,
): LayoutElement =
    Box
        .Builder()
        .setWidth(DimensionBuilders.expand())
        .setHeight(DimensionBuilders.expand())
        .setModifiers(
            Modifiers
                .Builder()
                .setBackground(Background.Builder().setColor(argb(COLOR_BACKGROUND)).build())
                .build(),
        ).addContent(
            Column
                .Builder()
                .setWidth(DimensionBuilders.expand())
                .setHeight(DimensionBuilders.expand())
                .setModifiers(
                    Modifiers
                        .Builder()
                        .setPadding(Padding.Builder().setAll(dp(12f)).build())
                        .build(),
                ).addContent(buildHeader())
                .addContent(buildDivider())
                .addContent(
                    if (favorites.isEmpty()) {
                        buildEmptyMessage(context)
                    } else {
                        buildFavoritesList(favorites)
                    },
                ).build(),
        ).build()

private fun buildHeader(): LayoutElement =
    Text
        .Builder()
        .setText("⭐ Favorites")
        .setFontStyle(
            FontStyle
                .Builder()
                .setSize(sp(14f))
                .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                .setColor(argb(COLOR_ON_SURFACE))
                .build(),
        ).build()

private fun buildDivider(): LayoutElement =
    Box
        .Builder()
        .setWidth(DimensionBuilders.expand())
        .setHeight(dp(1f))
        .setModifiers(
            Modifiers
                .Builder()
                .setBackground(Background.Builder().setColor(argb(COLOR_ACCENT)).build())
                .setPadding(
                    Padding
                        .Builder()
                        .setTop(dp(4f))
                        .setBottom(dp(4f))
                        .build(),
                ).build(),
        ).build()

private fun buildEmptyMessage(context: Context): LayoutElement =
    Text
        .Builder()
        .setText(context.getString(R.string.tile_no_favorites))
        .setFontStyle(
            FontStyle
                .Builder()
                .setSize(sp(12f))
                .setColor(argb(COLOR_SUBTITLE))
                .build(),
        ).build()

private fun buildFavoritesList(favorites: List<CollectionItem>): LayoutElement {
    val column =
        Column
            .Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())

    favorites.take(5).forEach { item ->
        column.addContent(buildFavoriteRow(item))
        column.addContent(Spacer.Builder().setHeight(dp(4f)).build())
    }
    return column.build()
}

private fun buildFavoriteRow(item: CollectionItem): LayoutElement =
    Row
        .Builder()
        .setWidth(DimensionBuilders.expand())
        .setHeight(DimensionBuilders.wrap())
        .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
        .setModifiers(
            Modifiers
                .Builder()
                .setClickable(
                    Clickable
                        .Builder()
                        .setOnClick(
                            ActionBuilders.LaunchAction
                                .Builder()
                                .setAndroidActivity(
                                    ActionBuilders.AndroidActivity
                                        .Builder()
                                        .setPackageName("com.pklein.bookmemo")
                                        .setClassName("com.pklein.bookmemokmp.MainActivity")
                                        .build(),
                                ).build(),
                        ).build(),
                ).setPadding(
                    Padding
                        .Builder()
                        .setTop(dp(3f))
                        .setBottom(dp(3f))
                        .build(),
                ).build(),
        ).addContent(
            Text
                .Builder()
                .setText(item.type.toEmoji())
                .setFontStyle(FontStyle.Builder().setSize(sp(12f)).build())
                .setModifiers(
                    Modifiers
                        .Builder()
                        .setPadding(Padding.Builder().setEnd(dp(6f)).build())
                        .build(),
                ).build(),
        ).addContent(
            Text
                .Builder()
                .setText(item.title.take(20))
                .setFontStyle(
                    FontStyle
                        .Builder()
                        .setSize(sp(12f))
                        .setColor(argb(COLOR_ON_SURFACE))
                        .build(),
                ).build(),
        ).build()

private fun ItemType.toEmoji() =
    when (this) {
        ItemType.LITERATURE -> "📘"
        ItemType.MANGA -> "🌸"
        ItemType.COMIC -> "💬"
    }
