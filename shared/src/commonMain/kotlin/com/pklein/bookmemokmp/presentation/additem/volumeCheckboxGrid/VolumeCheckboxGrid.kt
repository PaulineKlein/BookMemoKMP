package com.pklein.bookmemokmp.presentation.additem.volumeCheckboxGrid

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.down_accessibility
import bookmemokmp.shared.generated.resources.up_accessibility
import bookmemokmp.shared.generated.resources.volumes_check_all
import bookmemokmp.shared.generated.resources.volumes_section
import bookmemokmp.shared.generated.resources.volumes_uncheck_all
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

/**
 * Collapsible grid of numbered checkboxes for volume tracking.
 *
 * - If [totTome] is known (> 0), shows exactly that many fixed slots — no + button.
 * - If [totTome] is unknown, slot count is driven by [slotCount] from the parent (via + button
 *   or the "Last volume" field). Checking/unchecking boxes never changes the slot count.
 * - "Select all" / "Deselect all" operate on the currently visible slots.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VolumeCheckboxGrid(
    checkedTomes: List<Int>,
    totTome: Int?,
    slotCount: Int,
    onSlotCountChange: (Int) -> Unit,
    onCheckedTomesChange: (List<Int>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalKnown = (totTome ?: 0) > 0
    val displayCount = if (totalKnown) totTome!! else slotCount

    var expanded by rememberSaveable { mutableStateOf(false) }

    val onContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val summary = if (totalKnown) "${checkedTomes.size} / $displayCount" else "${checkedTomes.size}"

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .border(1.dp, onContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .animateContentSize(),
    ) {
        // ── Header (always visible) ───────────────────────────────────────────
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.volumes_section),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = onContainer,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.labelMedium,
                    color = onContainer.copy(alpha = 0.7f),
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription =
                        stringResource(
                            if (expanded) Res.string.up_accessibility else Res.string.down_accessibility,
                        ),
                    tint = onContainer.copy(alpha = 0.7f),
                )
            }
        }

        // ── Expanded content ──────────────────────────────────────────────────
        if (expanded) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
            ) {
                TextButton(onClick = { onCheckedTomesChange((1..displayCount).toList()) }) {
                    Text(
                        stringResource(Res.string.volumes_check_all),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                TextButton(
                    onClick = { onCheckedTomesChange(emptyList()) },
                    enabled = checkedTomes.isNotEmpty(),
                ) {
                    Text(
                        stringResource(Res.string.volumes_uncheck_all),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            FlowRow(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                for (vol in 1..displayCount) {
                    VolumeCheckbox(
                        volume = vol,
                        checked = vol in checkedTomes,
                        onToggle = { checked ->
                            val updated =
                                if (checked) {
                                    (checkedTomes + vol).sorted()
                                } else {
                                    checkedTomes - vol
                                }
                            onCheckedTomesChange(updated)
                        },
                    )
                }

                if (!totalKnown) {
                    IconButton(
                        onClick = { onSlotCountChange(slotCount + 1) },
                        modifier =
                            Modifier
                                .size(56.dp)
                                .align(Alignment.CenterVertically),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PreviewVolumeCheckboxGridCollapsed() {
    BookMemoTheme {
        VolumeCheckboxGrid(
            checkedTomes = listOf(1, 2, 3, 5),
            totTome = 10,
            slotCount = 10,
            onSlotCountChange = {},
            onCheckedTomesChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewVolumeCheckboxGridUnknownTotal() {
    BookMemoTheme {
        VolumeCheckboxGrid(
            checkedTomes = listOf(1, 2),
            totTome = null,
            slotCount = 3,
            onSlotCountChange = {},
            onCheckedTomesChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewVolumeCheckboxGridEmpty() {
    BookMemoTheme {
        VolumeCheckboxGrid(
            checkedTomes = emptyList(),
            totTome = null,
            slotCount = 1,
            onSlotCountChange = {},
            onCheckedTomesChange = {},
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewVolumeCheckboxGridBigFont() {
    BookMemoTheme {
        VolumeCheckboxGrid(
            checkedTomes = listOf(1, 3),
            totTome = 6,
            slotCount = 6,
            onSlotCountChange = {},
            onCheckedTomesChange = {},
        )
    }
}
