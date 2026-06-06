package com.pklein.bookmemokmp.presentation.additem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.add_item
import bookmemokmp.shared.generated.resources.add_to_collection
import bookmemokmp.shared.generated.resources.author
import bookmemokmp.shared.generated.resources.bought
import bookmemokmp.shared.generated.resources.delete_accessibility
import bookmemokmp.shared.generated.resources.description
import bookmemokmp.shared.generated.resources.edit_item
import bookmemokmp.shared.generated.resources.edit_title
import bookmemokmp.shared.generated.resources.favorite
import bookmemokmp.shared.generated.resources.favorite_ios
import bookmemokmp.shared.generated.resources.finished
import bookmemokmp.shared.generated.resources.go_back_accessibility
import bookmemokmp.shared.generated.resources.illustrator
import bookmemokmp.shared.generated.resources.last_chapter
import bookmemokmp.shared.generated.resources.last_episode
import bookmemokmp.shared.generated.resources.last_season
import bookmemokmp.shared.generated.resources.last_volume
import bookmemokmp.shared.generated.resources.loan_borrowed_by
import bookmemokmp.shared.generated.resources.loan_borrowed_since
import bookmemokmp.shared.generated.resources.loaned
import bookmemokmp.shared.generated.resources.progress_section
import bookmemokmp.shared.generated.resources.publication_year
import bookmemokmp.shared.generated.resources.remove_cover_accessibility
import bookmemokmp.shared.generated.resources.scan_barcode
import bookmemokmp.shared.generated.resources.search_online
import bookmemokmp.shared.generated.resources.searching
import bookmemokmp.shared.generated.resources.title_required
import bookmemokmp.shared.generated.resources.type_book
import bookmemokmp.shared.generated.resources.type_comic
import bookmemokmp.shared.generated.resources.type_manga
import bookmemokmp.shared.generated.resources.type_section
import bookmemokmp.shared.generated.resources.update_item
import bookmemokmp.shared.generated.resources.wishlist
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.isAndroidPlatform
import com.pklein.bookmemokmp.presentation.additem.viewmodel.AddItemViewModel
import com.pklein.bookmemokmp.presentation.additem.viewmodel.SearchState
import com.pklein.bookmemokmp.scanner.BarcodeScanner
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel,
    onSave: (CollectionItem) -> Unit,
    onBack: () -> Unit,
    initialItem: CollectionItem? = null,
    onDelete: (() -> Unit)? = null,
    barcodeScanner: BarcodeScanner? = null,
) {
    val searchState by viewModel.searchState.collectAsState()

    AddItemScreenContent(
        onSave = onSave,
        onBack = onBack,
        initialItem = initialItem,
        searchState = searchState,
        onDelete = onDelete,
        onSearch = viewModel::search,
        onSearchIsbn = viewModel::searchByIsbn,
        onCheckDuplicate = viewModel::existsByTitleAndType,
        initialSaveDescription = viewModel.englishDescriptionPref,
        onSaveDescriptionChanged = { viewModel.englishDescriptionPref = it },
        barcodeScanner = barcodeScanner,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemScreenContent(
    onSave: (CollectionItem) -> Unit,
    onBack: () -> Unit,
    initialItem: CollectionItem? = null,
    searchState: SearchState = SearchState.Idle,
    onDelete: (() -> Unit)? = null,
    onSearch: (query: String, type: ItemType, langRestrict: String?) -> Unit,
    onSearchIsbn: (isbn: String?) -> Unit,
    onCheckDuplicate: suspend (title: String, type: ItemType, excludeId: Long) -> Boolean,
    initialSaveDescription: Boolean = true,
    onSaveDescriptionChanged: (Boolean) -> Unit = {},
    barcodeScanner: BarcodeScanner? = null,
) {
    val scope = rememberCoroutineScope()

    val isEditing = initialItem != null

    var showDeleteDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf<String?>(null) }
    var showResultsDialog by remember { mutableStateOf(false) }
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var pendingSaveItem by remember { mutableStateOf<CollectionItem?>(null) }
    var isCheckingDuplicate by remember { mutableStateOf(false) }
    var pendingDescriptionResult by remember { mutableStateOf<SearchResult?>(null) }

    var type by remember { mutableStateOf(initialItem?.type ?: ItemType.LITERATURE) }
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var author by remember { mutableStateOf(initialItem?.author ?: "") }
    var illustrator by remember { mutableStateOf(initialItem?.illustrator ?: "") }
    var year by remember { mutableStateOf(initialItem?.year?.toString() ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var bought by remember { mutableStateOf(initialItem?.bought ?: false) }
    var wishlist by remember { mutableStateOf(initialItem?.wishlist ?: false) }
    var favorite by remember { mutableStateOf(initialItem?.favorite ?: false) }
    var finished by remember { mutableStateOf(initialItem?.finished ?: false) }
    var tome by remember { mutableStateOf(initialItem?.tome?.toString() ?: "") }
    var chapter by remember { mutableStateOf(initialItem?.chapter?.toString() ?: "") }
    var episode by remember { mutableStateOf(initialItem?.episode?.toString() ?: "") }
    var season by remember { mutableStateOf(initialItem?.season?.toString() ?: "") }
    var titleError by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf(initialItem?.imageUrl ?: "") }
    var isBorrowed by remember { mutableStateOf(initialItem?.isBorrowed ?: false) }
    var borrowedBy by remember { mutableStateOf(initialItem?.borrowedBy ?: "") }
    var jikanId by remember { mutableStateOf(initialItem?.jikanId) }
    var jikanType by remember { mutableStateOf(initialItem?.jikanType) }
    var totTome by remember { mutableStateOf(initialItem?.totTome) }
    var totChapter by remember { mutableStateOf(initialItem?.totChapter) }
    var totEpisode by remember { mutableStateOf(initialItem?.totEpisode) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialItem?.borrowedSince,
        )
    val borrowedSinceLabel =
        datePickerState.selectedDateMillis?.let { millis ->
            val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
            "${date.day.toString().padStart(2, '0')}/${
                date.month.number.toString().padStart(2, '0')
            }/${date.year}"
        } ?: ""

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = { onDelete?.invoke() },
            onDismiss = { showDeleteDialog = false },
        )
    }

    val showEnglishNotice = type == ItemType.MANGA && Locale.current.language == "fr"

    if (showResultsDialog) {
        SearchResultsDialog(
            searchState = searchState,
            query = searchQuery,
            showEnglishNotice = showEnglishNotice,
            initialSaveDescription = initialSaveDescription,
            onSelect = { result, shouldSaveDescription ->
                if (showEnglishNotice) onSaveDescriptionChanged(shouldSaveDescription)
                // Apply non-description fields immediately
                title = result.title
                result.author?.let { author = it }
                result.year?.let { year = it.toString() }
                imageUrl = result.imageUrl ?: ""
                jikanId = result.jikanId
                jikanType = result.jikanType
                totTome = result.totTome
                totChapter = result.totChapter
                totEpisode = result.totEpisode
                titleError = false
                showResultsDialog = false
                // Description: ask the user if there is already content
                if (shouldSaveDescription) {
                    if (description.isNotBlank() && result.description != null) {
                        pendingDescriptionResult = result
                    } else {
                        result.description?.let { description = it }
                    }
                }
            },
            onDismiss = {
                showResultsDialog = false
            },
        )
    }

    if (showDuplicateDialog) {
        DuplicateWarningDialog(
            onConfirm = {
                pendingSaveItem?.let { onSave(it) }
                showDuplicateDialog = false
                pendingSaveItem = null
            },
            onDismiss = {
                showDuplicateDialog = false
                pendingSaveItem = null
            },
        )
    }

    pendingDescriptionResult?.let { result ->
        DescriptionConflictDialog(
            onReplace = {
                description = result.description ?: description
                pendingDescriptionResult = null
            },
            onConcatenate = {
                result.description?.let { description = "$description\n$it" }
                pendingDescriptionResult = null
            },
            onCancel = { pendingDescriptionResult = null },
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) {
                            stringResource(Res.string.edit_item)
                        } else {
                            stringResource(Res.string.add_to_collection)
                        },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.go_back_accessibility),
                        )
                    }
                },
                actions = {
                    if (onDelete != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(Res.string.delete_accessibility),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding(),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // ── Type ─────────────────────────────────────────────────────────
                Text(
                    stringResource(Res.string.type_section),
                    style = MaterialTheme.typography.headlineSmall,
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ItemType.entries.forEachIndexed { index, itemType ->
                        SegmentedButton(
                            selected = type == itemType,
                            onClick = { type = itemType },
                            shape = SegmentedButtonDefaults.itemShape(index, ItemType.entries.size),
                        ) {
                            Text(
                                when (itemType) {
                                    ItemType.LITERATURE -> stringResource(Res.string.type_book)
                                    ItemType.MANGA -> stringResource(Res.string.type_manga)
                                    ItemType.COMIC -> stringResource(Res.string.type_comic)
                                },
                            )
                        }
                    }
                }

                // ── Basic info ───────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box {
                        CoverPreviewItem(
                            imageUrl = imageUrl,
                            modifier =
                                Modifier
                                    .width(132.dp)
                                    .height(180.dp),
                        )
                        if (imageUrl.isNotBlank()) {
                            Box(
                                modifier =
                                    Modifier
                                        .align(Alignment.TopEnd)
                                        .size(32.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            CircleShape,
                                        ).border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline,
                                            CircleShape,
                                        ).clickable { imageUrl = "" },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(Res.string.remove_cover_accessibility),
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                titleError = false
                            },
                            label = { Text(stringResource(Res.string.edit_title)) },
                            modifier = Modifier.fillMaxWidth(),
                            isError = titleError,
                            supportingText =
                                if (titleError) {
                                    { Text(stringResource(Res.string.title_required)) }
                                } else {
                                    null
                                },
                            singleLine = true,
                        )
                        Button(
                            onClick = {
                                showResultsDialog = true
                                onSearch(
                                    title.trim(),
                                    type,
                                    if (Locale.current.language == "fr") "fr" else "en",
                                )
                            },
                            enabled = title.isNotBlank() && searchState !is SearchState.Loading,
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                        ) {
                            if (searchState is SearchState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                )
                                Text(
                                    stringResource(Res.string.searching),
                                    modifier = Modifier.padding(start = 8.dp),
                                )
                            } else {
                                Text(stringResource(Res.string.search_online))
                            }
                        }
                        if (barcodeScanner != null) {
                            Button(
                                onClick = {
                                    barcodeScanner.scan(
                                        onResult = { isbn ->
                                            title = isbn
                                            titleError = false
                                            showResultsDialog = true
                                            onSearchIsbn(isbn)
                                        },
                                        onError = {
                                            showResultsDialog = true
                                            onSearchIsbn(null)
                                        },
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                                Text(
                                    stringResource(Res.string.scan_barcode),
                                    modifier = Modifier.padding(start = 4.dp),
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text(stringResource(Res.string.author)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                if (type != ItemType.LITERATURE) {
                    OutlinedTextField(
                        value = illustrator,
                        onValueChange = { illustrator = it },
                        label = { Text(stringResource(Res.string.illustrator)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                OutlinedTextField(
                    value = year,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) year = it },
                    label = { Text(stringResource(Res.string.publication_year)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(Res.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )

                // ── Progress ─────────────────────────────────────────────────────
                Text(
                    stringResource(Res.string.progress_section),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 8.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = tome,
                        onValueChange = { if (it.all(Char::isDigit)) tome = it },
                        label = { Text(stringResource(Res.string.last_volume)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = chapter,
                        onValueChange = { if (it.all(Char::isDigit)) chapter = it },
                        label = { Text(stringResource(Res.string.last_chapter)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = season,
                        onValueChange = { if (it.all(Char::isDigit)) season = it },
                        label = { Text(stringResource(Res.string.last_season)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = episode,
                        onValueChange = { if (it.all(Char::isDigit)) episode = it },
                        label = { Text(stringResource(Res.string.last_episode)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }

                // ── Toggles ──────────────────────────────────────────────────────
                ToggleRowItem(
                    label = stringResource(if (isAndroidPlatform) Res.string.favorite else Res.string.favorite_ios),
                    checked = favorite,
                    onCheckedChange = { favorite = it },
                )
                ToggleRowItem(
                    label = stringResource(Res.string.bought),
                    checked = bought,
                    onCheckedChange = { bought = it },
                )
                ToggleRowItem(
                    label = stringResource(Res.string.wishlist),
                    checked = wishlist,
                    onCheckedChange = { wishlist = it },
                )
                ToggleRowItem(
                    label = stringResource(Res.string.finished),
                    checked = finished,
                    onCheckedChange = { finished = it },
                )

                // ── Loan ─────────────────────────────────────────────────────────
                ToggleRowItem(
                    label = stringResource(Res.string.loaned),
                    checked = isBorrowed,
                    onCheckedChange = { isBorrowed = it },
                )
                if (isBorrowed) {
                    OutlinedTextField(
                        value = borrowedBy,
                        onValueChange = { borrowedBy = it },
                        label = { Text(stringResource(Res.string.loan_borrowed_by)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = borrowedSinceLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(Res.string.loan_borrowed_since)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Box(
                            modifier =
                                Modifier
                                    .matchParentSize()
                                    .clickable { showDatePicker = true },
                        )
                    }
                }
            }

            // ── Save ─────────────────────────────────────────────────────────
            Button(
                shape = RoundedCornerShape(20),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                enabled = !isCheckingDuplicate,
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@Button
                    }
                    val item =
                        CollectionItem(
                            id = initialItem?.id ?: 0,
                            type = type,
                            title = title.trim(),
                            author = author.trim().ifBlank { null },
                            illustrator = illustrator.trim().ifBlank { null },
                            year = year.toIntOrNull(),
                            description = description.trim().ifBlank { null },
                            bought = bought,
                            wishlist = wishlist,
                            favorite = favorite,
                            finished = finished,
                            tome = tome.toIntOrNull(),
                            chapter = chapter.toIntOrNull(),
                            episode = episode.toIntOrNull(),
                            season = season.toIntOrNull(),
                            imageUrl = imageUrl.trim().ifBlank { null },
                            isBorrowed = isBorrowed,
                            borrowedBy =
                                if (isBorrowed) {
                                    borrowedBy
                                        .trim()
                                        .ifBlank { null }
                                } else {
                                    null
                                },
                            borrowedSince = if (isBorrowed) datePickerState.selectedDateMillis else null,
                            jikanId = jikanId,
                            jikanType = jikanType,
                            totTome = totTome,
                            totChapter = totChapter,
                            totEpisode = totEpisode,
                        )
                    scope.launch {
                        isCheckingDuplicate = true
                        try {
                            val isDuplicate =
                                onCheckDuplicate(item.title, item.type, initialItem?.id ?: 0L)
                            if (isDuplicate) {
                                pendingSaveItem = item
                                showDuplicateDialog = true
                            } else {
                                onSave(item)
                            }
                        } finally {
                            isCheckingDuplicate = false
                        }
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text =
                        if (isEditing) {
                            stringResource(Res.string.update_item)
                        } else {
                            stringResource(Res.string.add_item)
                        },
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

/** Empty form — default Book type. */
@Preview
@Composable
private fun PreviewAddItemScreenEmpty() {
    BookMemoTheme {
        AddItemScreenContent(
            onSave = {},
            onBack = {},
            onSearch = { _, _, _ -> },
            onSearchIsbn = {},
            onCheckDuplicate = { _, _, _ -> false },
        )
    }
}

/** Pre-filled form — edit mode. */
@Preview
@Composable
private fun PreviewAddItemScreenEdit() {
    BookMemoTheme {
        AddItemScreenContent(
            onSave = {},
            onBack = {},
            onDelete = {},
            onSearch = { _, _, _ -> },
            onSearchIsbn = {},
            onCheckDuplicate = { _, _, _ -> false },
            initialItem =
                CollectionItem(
                    id = 1,
                    type = ItemType.MANGA,
                    title = "One Piece",
                    author = "Eiichiro Oda",
                    imageUrl = "https://example.com/cover.jpg",
                    year = 1997,
                    description = "A pirate adventure manga.",
                    bought = true,
                    wishlist = true,
                    favorite = true,
                    tome = 107,
                    chapter = 1100,
                    episode = 1090,
                    season = 1,
                ),
        )
    }
}

@Preview(fontScale = 2.0f)
@Composable
private fun PreviewAddItemScreenEditBigFont() {
    BookMemoTheme {
        AddItemScreenContent(
            onSave = {},
            onBack = {},
            onDelete = {},
            onSearch = { _, _, _ -> },
            onSearchIsbn = {},
            onCheckDuplicate = { _, _, _ -> false },
            initialItem =
                CollectionItem(
                    id = 1,
                    type = ItemType.MANGA,
                    title = "One Piece",
                    author = "Eiichiro Oda",
                    imageUrl = "https://example.com/cover.jpg",
                    year = 1997,
                    description = "A pirate adventure manga.",
                    bought = true,
                    favorite = true,
                    tome = 107,
                    chapter = 1100,
                    episode = 1090,
                    season = 1,
                ),
        )
    }
}

/** Title-error state: save was attempted with a blank title. */
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewAddItemScreenTitleError() {
    BookMemoTheme {
        // Simulate the error by rendering the screen and immediately
        // triggering the validation path through a wrapper state.
        var titleError by remember { mutableStateOf(true) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add to collection") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back",
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = { titleError = false },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError,
                    supportingText = { Text("Title is required") },
                    singleLine = true,
                )
            }
        }
    }
}
