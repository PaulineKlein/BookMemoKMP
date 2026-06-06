package com.pklein.bookmemokmp.presentation.collection.filter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

// ── Type filter row (single select) ───────────────────────────────────────────
@Composable
fun FilterRow(
    activeFilter: CollectionFilter,
    onFilterChange: (CollectionFilter) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(CollectionFilter.entries) { filter ->
            ElevatedFilterChip(
                selected = activeFilter == filter,
                onClick = { onFilterChange(filter) },
                label = {
                    Text(
                        text = stringResource(filter.label),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )
        }
    }
}

// ── Status filter row (tri-state: ALL → YES → NO) ────────────────────────────
@Composable
fun SubFilterRow(
    activeStatusFilters: StatusFilters,
    onStatusFilterCycle: (StatusFilterField) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(StatusFilterField.entries) { field ->
            val state = field.get(activeStatusFilters)
            val selected = state != TriState.ALL
            FilterChip(
                selected = selected,
                onClick = { onStatusFilterCycle(field) },
                label = {
                    Text(
                        text = stringResource(
                            if (state == TriState.NO) field.labelNo else field.labelYes
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                },
                leadingIcon = when (state) {
                    TriState.YES -> {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    }

                    TriState.NO -> {
                        {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    TriState.ALL -> null
                }
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewFilterRow() {
    BookMemoTheme {
        FilterRow(
            activeFilter = CollectionFilter.COMIC,
            onFilterChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSubFilterRow() {
    BookMemoTheme {
        SubFilterRow(
            activeStatusFilters = StatusFilters(bought = TriState.YES, favorites = TriState.NO),
            onStatusFilterCycle = {}
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewFilterRowBigFont() {
    BookMemoTheme {
        FilterRow(
            activeFilter = CollectionFilter.COMIC,
            onFilterChange = {}
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewSubFilterRowBigFont() {
    BookMemoTheme {
        SubFilterRow(
            activeStatusFilters = StatusFilters(finished = TriState.NO),
            onStatusFilterCycle = {}
        )
    }
}