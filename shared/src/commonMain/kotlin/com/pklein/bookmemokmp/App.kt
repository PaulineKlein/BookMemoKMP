package com.pklein.bookmemokmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.navigation.Screen
import com.pklein.bookmemokmp.presentation.additem.AddItemScreen
import com.pklein.bookmemokmp.presentation.additem.viewmodel.AddItemViewModel
import com.pklein.bookmemokmp.presentation.collection.CollectionScreen
import com.pklein.bookmemokmp.presentation.collection.viewmodel.CollectionViewModel
import com.pklein.bookmemokmp.scanner.BarcodeScanner
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    startEditItemId: Long? = null,
    onExportCsv: (String) -> Unit = {},
    onExportDb: () -> Unit = {},
    onImportDb: () -> Unit = {},
    barcodeScanner: BarcodeScanner? = null,
) {
    val collectionViewModel: CollectionViewModel = koinViewModel()
    val addItemViewModel: AddItemViewModel = koinViewModel()
    val navController = rememberNavController()
    var editingItem by remember { mutableStateOf<CollectionItem?>(null) }

    // Triggered by a widget tap: load the item and open the edit screen.
    // Uses startEditItemId as the key so it re-runs whenever a different
    // item is tapped (including after onNewIntent when the app is already open).
    LaunchedEffect(startEditItemId) {
        if (startEditItemId != null) {
            val item = collectionViewModel.getById(startEditItemId)
            if (item != null) {
                editingItem = item
                navController.navigate(Screen.EditItem.route)
            }
        }
    }

    BookMemoTheme {
        NavHost(
            navController = navController,
            startDestination = Screen.Collection.route,
        ) {
            composable(Screen.Collection.route) {
                CollectionScreen(
                    viewModel = collectionViewModel,
                    onAddClick = { navController.navigate(Screen.AddItem.route) },
                    onEditClick = { item ->
                        editingItem = item
                        navController.navigate(Screen.EditItem.route)
                    },
                    onExportCsv = onExportCsv,
                    onExportDb = onExportDb,
                    onImportDb = onImportDb,
                )
            }

            composable(Screen.AddItem.route) {
                AddItemScreen(
                    viewModel = addItemViewModel,
                    onSave = { item ->
                        collectionViewModel.add(item)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() },
                    barcodeScanner = barcodeScanner,
                )
            }

            composable(Screen.EditItem.route) {
                // editingItem is always set before navigating here
                editingItem?.let { item ->
                    AddItemScreen(
                        viewModel = addItemViewModel,
                        initialItem = item,
                        onSave = { updated ->
                            collectionViewModel.update(updated)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() },
                        onDelete = {
                            collectionViewModel.delete(item.id)
                            navController.popBackStack()
                        },
                        barcodeScanner = barcodeScanner,
                    )
                }
            }
        }
    }
}
