package com.pklein.bookmemokmp.presentation.additem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme

private val zoomOneRegex = Regex("zoom=1(?!\\d)")

/** Google Books adds a page-curl effect on the corner of covers with this parameter. */
internal fun String.withoutPageCurl(): String = replace("&edge=curl", "")

/** Google Books covers are saved as zoom=1 (~128px); zoom=2 is sharper for large display. */
internal fun String.withHigherZoom(): String? = if (zoomOneRegex.containsMatchIn(this)) replace(zoomOneRegex, "zoom=2") else null

/**
 * When [tryHigherZoom] is true and [imageUrl] is a Google Books thumbnail, loads a sharper
 * version first and falls back to [imageUrl] if that request fails.
 */
@Composable
fun CoverPreviewItem(
    imageUrl: String,
    modifier: Modifier = Modifier,
    tryHigherZoom: Boolean = false,
) {
    val shape = RoundedCornerShape(8.dp)
    val cleanUrl = imageUrl.withoutPageCurl()
    val higherZoomUrl = if (tryHigherZoom) cleanUrl.withHigherZoom() else null
    var higherZoomFailed by remember(imageUrl) { mutableStateOf(false) }
    val modelUrl = if (higherZoomUrl != null && !higherZoomFailed) higherZoomUrl else cleanUrl
    SubcomposeAsyncImage(
        model = modelUrl.ifBlank { null },
        contentDescription = null,
        contentScale = ContentScale.Fit,
        onError = { if (higherZoomUrl != null) higherZoomFailed = true },
        loading = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            }
        },
        error = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector =
                        if (imageUrl.isBlank()) {
                            Icons.Outlined.Photo
                        } else {
                            Icons.Outlined.ErrorOutline
                        },
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        modifier = modifier.clip(shape),
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

/** Shows the placeholder (photo icon) — no network in preview. */
@Preview(showBackground = true)
@Composable
private fun PreviewCoverPreviewItemEmpty() {
    BookMemoTheme {
        CoverPreviewItem(
            imageUrl = "",
            modifier = Modifier.size(80.dp, 112.dp),
        )
    }
}

/** Simulates a real URL — also renders the error placeholder in preview. */
@Preview(showBackground = true)
@Composable
private fun PreviewCoverPreviewItemWithUrl() {
    BookMemoTheme {
        CoverPreviewItem(
            imageUrl = "https://example.com/cover.jpg",
            modifier = Modifier.size(80.dp, 112.dp),
        )
    }
}
