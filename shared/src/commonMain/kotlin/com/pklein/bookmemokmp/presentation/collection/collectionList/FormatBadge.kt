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
import org.jetbrains.compose.resources.stringResource

@Composable
fun FormatBadge(format: FormatType) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Text(
        text = stringResource(format.stringRes),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewFormatBadgeNovel() {
    BookMemoTheme { FormatBadge(FormatType.NOVEL) }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFormatBadgeWebtoon() {
    BookMemoTheme { FormatBadge(FormatType.WEBTOON) }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFormatBadgeFrancoBelgian() {
    BookMemoTheme { FormatBadge(FormatType.FRANCO_BELGIAN_COMIC) }
}
