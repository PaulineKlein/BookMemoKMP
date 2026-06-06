package com.pklein.bookmemokmp.presentation.additem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme

@Composable
fun ToggleRowItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/** Toggle row with switch off. */
@Preview(showBackground = true)
@Composable
private fun PreviewToggleRowOff() {
    BookMemoTheme {
        ToggleRowItem(label = "Favorite", checked = false, onCheckedChange = {})
    }
}

/** Toggle row with switch on. */
@Preview(showBackground = true)
@Composable
private fun PreviewToggleRowOn() {
    BookMemoTheme {
        ToggleRowItem(label = "Bought", checked = true, onCheckedChange = {})
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewToggleRowOnBigFont() {
    BookMemoTheme {
        ToggleRowItem(
            label = "Bought with very very very very long title",
            checked = true,
            onCheckedChange = {})
    }
}
