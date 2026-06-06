package com.pklein.bookmemokmp.presentation.collection.cover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.book_1
import coil3.compose.SubcomposeAsyncImage
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun CoverSmallPreviewItem(imageUrl: String?) {
    SubcomposeAsyncImage(
        model = imageUrl?.ifBlank { null },
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(40.dp, 56.dp)
            .clip(RoundedCornerShape(4.dp)),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        },
        error = {
            Image(
                painter = painterResource(Res.drawable.book_1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp, 56.dp)
            )
        }
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

/** Shows the placeholder (photo icon) — no network in preview. */
@Preview
@Composable
private fun PreviewCoverPreviewItemEmpty() {
    BookMemoTheme {
        CoverSmallPreviewItem(
            imageUrl = ""
        )
    }
}

/** Simulates a real URL — also renders the error placeholder in preview. */
@Preview
@Composable
private fun PreviewCoverPreviewItemWithUrlFailed() {
    BookMemoTheme {
        CoverSmallPreviewItem(
            imageUrl = "https://example.com/cover.jpg"
        )
    }
}

