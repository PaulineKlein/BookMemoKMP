package com.pklein.bookmemokmp.presentation.collection.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.add_item_menu
import bookmemokmp.shared.generated.resources.discover_manga
import bookmemokmp.shared.generated.resources.export_csv
import bookmemokmp.shared.generated.resources.export_database
import bookmemokmp.shared.generated.resources.export_menu_accessibility
import bookmemokmp.shared.generated.resources.import_database
import com.pklein.bookmemokmp.isAndroidPlatform
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource


@Composable
fun MenuItem(
    onAddBook: () -> Unit,
    onShowDiscoverSheet: () -> Unit,
    onExportCsv: () -> Unit,
    onExportDb: () -> Unit,
    onImportDb: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(
                imageVector = if (isAndroidPlatform) Icons.Default.MoreVert else Icons.Default.MoreHoriz,
                contentDescription = stringResource(Res.string.export_menu_accessibility)
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.add_item_menu)) },
                onClick = { onAddBook(); menuExpanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.discover_manga)) },
                onClick = { onShowDiscoverSheet(); menuExpanded = false }
            )
            if (isAndroidPlatform) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.export_csv)) },
                    onClick = { onExportCsv(); menuExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.export_database)) },
                    onClick = { onExportDb(); menuExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.import_database)) },
                    onClick = { onImportDb(); menuExpanded = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMenuItem() {
    BookMemoTheme {
        MenuItem(
            onAddBook = {},
            onShowDiscoverSheet = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {}
        )
    }
}

@Preview(showBackground = true, fontScale = 2.0f)
@Composable
private fun PreviewMenuItemBigFont() {
    BookMemoTheme {
        MenuItem(
            onAddBook = {},
            onShowDiscoverSheet = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {}
        )
    }
}
