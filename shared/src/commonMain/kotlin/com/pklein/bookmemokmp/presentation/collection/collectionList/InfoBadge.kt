package com.pklein.bookmemokmp.presentation.collection.collectionList

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pklein.bookmemokmp.domain.model.FormatType
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun InfoBadge(title: StringResource) {
    val color = MaterialTheme.colorScheme.onPrimaryContainer
    Text(
        text = stringResource(title),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier =
            Modifier
                .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(
                    horizontal = 10.dp,
                    vertical = 4.dp,
                ),
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewInfoBadgeNovel() {
    BookMemoTheme { InfoBadge(FormatType.NOVEL.stringRes) }
}

@Preview(showBackground = true)
@Composable
private fun PreviewInfoBadgeWebtoon() {
    BookMemoTheme { InfoBadge(FormatType.WEBTOON.stringRes) }
}

@Preview(showBackground = true)
@Composable
private fun PreviewInfoBadgeFrancoBelgian() {
    BookMemoTheme { InfoBadge(FormatType.FRANCO_BELGIAN_COMIC.stringRes) }
}
