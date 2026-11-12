package com.pklein.bookmemokmp.presentation.collection.collectionList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.FilterListOff
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.empty_collection_action
import bookmemokmp.shared.generated.resources.empty_collection_description
import bookmemokmp.shared.generated.resources.empty_collection_title
import bookmemokmp.shared.generated.resources.empty_filters_action
import bookmemokmp.shared.generated.resources.empty_filters_description
import bookmemokmp.shared.generated.resources.empty_filters_title
import bookmemokmp.shared.generated.resources.empty_search_action
import bookmemokmp.shared.generated.resources.empty_search_description
import bookmemokmp.shared.generated.resources.search_empty
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EmptyState(
    title: String,
    description: String,
    illustration: @Composable () -> Unit,
    action: @Composable () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        illustration()
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
        action()
    }
}

@Composable
internal fun EmptyStateIcon(imageVector: ImageVector) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.size(96.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(44.dp),
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

/** Empty collection — first launch. */
@Preview(showBackground = true)
@Composable
private fun PreviewEmptyStateCollection() {
    BookMemoTheme {
        EmptyState(
            title = stringResource(Res.string.empty_collection_title),
            description = stringResource(Res.string.empty_collection_description),
            illustration = { EmptyStateIcon(Icons.Outlined.AutoStories) },
            action = {
                FilledTonalButton(onClick = {}) {
                    Text(stringResource(Res.string.empty_collection_action))
                }
            },
        )
    }
}

/** Status filters match nothing. */
@Preview(showBackground = true)
@Composable
private fun PreviewEmptyStateFilters() {
    BookMemoTheme {
        EmptyState(
            title = stringResource(Res.string.empty_filters_title),
            description = stringResource(Res.string.empty_filters_description),
            illustration = { EmptyStateIcon(Icons.Outlined.FilterListOff) },
            action = {
                FilledTonalButton(onClick = {}) {
                    Text(stringResource(Res.string.empty_filters_action))
                }
            },
        )
    }
}

/** Search returned no results. */
@Preview(showBackground = true)
@Composable
private fun PreviewEmptyStateSearch() {
    BookMemoTheme {
        EmptyState(
            title = stringResource(Res.string.search_empty, "Dune"),
            description = stringResource(Res.string.empty_search_description),
            illustration = { EmptyStateIcon(Icons.Outlined.SearchOff) },
            action = {
                TextButton(onClick = {}) {
                    Text(stringResource(Res.string.empty_search_action))
                }
            },
        )
    }
}

/** Empty collection — dark theme, big font. */
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewEmptyStateCollectionDarkBigFont() {
    BookMemoTheme {
        EmptyState(
            title = stringResource(Res.string.empty_collection_title),
            description = stringResource(Res.string.empty_collection_description),
            illustration = { EmptyStateIcon(Icons.Outlined.AutoStories) },
            action = {
                FilledTonalButton(onClick = {}) {
                    Text(stringResource(Res.string.empty_collection_action))
                }
            },
        )
    }
}
