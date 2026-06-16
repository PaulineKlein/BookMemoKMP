package com.pklein.bookmemokmp.presentation.additem.volumeCheckboxGrid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme

@Composable
fun VolumeCheckbox(
    volume: Int,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(48.dp),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onToggle,
        )
        Text(
            text = volume.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VolumeCheckboxPreview_Unchecked() {
    BookMemoTheme {
        VolumeCheckbox(volume = 1, checked = false, onToggle = {})
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun VolumeCheckboxPreview_checked_big_font() {
    BookMemoTheme {
        VolumeCheckbox(volume = 1, checked = true, onToggle = {})
    }
}
