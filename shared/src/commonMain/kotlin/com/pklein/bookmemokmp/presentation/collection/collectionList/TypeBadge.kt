package com.pklein.bookmemokmp.presentation.collection.collectionList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.type_book
import bookmemokmp.shared.generated.resources.type_comic
import bookmemokmp.shared.generated.resources.type_manga
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.ui.theme.BadgeBookColor
import com.pklein.bookmemokmp.ui.theme.BadgeComicColor
import com.pklein.bookmemokmp.ui.theme.BadgeMangaColor
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun TypeBadge(type: ItemType) {
    val (bgColor, label) = when (type) {
        ItemType.LITERATURE -> BadgeBookColor to stringResource(Res.string.type_book)
        ItemType.MANGA -> BadgeMangaColor to stringResource(Res.string.type_manga)
        ItemType.COMIC -> BadgeComicColor to stringResource(Res.string.type_comic)
    }
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = label, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewTypeBadgeBook() {
    BookMemoTheme {
        TypeBadge(
            type = ItemType.LITERATURE
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewTypeBadgeBookBigFont() {
    BookMemoTheme {
        TypeBadge(
            type = ItemType.LITERATURE
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTypeBadgeManga() {
    BookMemoTheme {
        TypeBadge(
            type = ItemType.MANGA
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewTypeBadgeMangaBigFont() {
    BookMemoTheme {
        TypeBadge(
            type = ItemType.MANGA
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTypeBadgeComic() {
    BookMemoTheme {
        TypeBadge(
            type = ItemType.COMIC
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewTypeBadgeComicBigFont() {
    BookMemoTheme {
        TypeBadge(
            type = ItemType.COMIC
        )
    }
}
