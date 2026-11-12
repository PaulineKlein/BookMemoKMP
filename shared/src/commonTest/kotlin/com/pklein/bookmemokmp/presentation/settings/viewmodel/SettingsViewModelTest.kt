package com.pklein.bookmemokmp.presentation.settings.viewmodel

import app.cash.sqldelight.db.QueryResult
import com.pklein.bookmemokmp.FakeSettings
import com.pklein.bookmemokmp.data.ThemeMode
import com.pklein.bookmemokmp.data.UserPreferencesRepository
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.repository.IBackupRepository
import com.pklein.bookmemokmp.domain.repository.ICollectionRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// ── Sample data ────────────────────────────────────────────────────────────────

private val naruto = CollectionItem(id = 10, type = ItemType.MANGA, title = "Naruto", tome = 1)
private val dune = CollectionItem(id = 11, type = ItemType.LITERATURE, title = "Dune")
private val remoteNaruto = CollectionItem(id = 0, type = ItemType.MANGA, title = "Naruto", tome = 5)
private val remoteOnePiece = CollectionItem(id = 0, type = ItemType.MANGA, title = "One Piece")

// ── Fakes ──────────────────────────────────────────────────────────────────────

private class FakeBackupRepository : IBackupRepository {
    var currentEmail: String? = null
    var lastBackupDate: String? = null
    var remoteItems: List<CollectionItem> = emptyList()
    var shouldFail = false

    /** When set, backup/restore/delete suspend until this is completed. */
    var gate: CompletableDeferred<Unit>? = null

    var signOutCalls = 0
    var deleteCalls = 0
    var backedUpItems: List<CollectionItem>? = null

    override fun getCurrentUserEmail(): String? = currentEmail

    override suspend fun signIn(webClientId: String) = Unit

    override suspend fun signOut() {
        signOutCalls++
    }

    override suspend fun fetchLastBackupDate(): String? = lastBackupDate

    override suspend fun backup(items: List<CollectionItem>) {
        gate?.await()
        if (shouldFail) throw RuntimeException("backup failed")
        backedUpItems = items
    }

    override suspend fun restore(): List<CollectionItem> {
        gate?.await()
        if (shouldFail) throw RuntimeException("restore failed")
        return remoteItems
    }

    override suspend fun deleteBackup() {
        gate?.await()
        if (shouldFail) throw RuntimeException("delete failed")
        deleteCalls++
    }
}

private class FakeCollectionRepository(
    initialItems: List<CollectionItem> = emptyList(),
) : ICollectionRepository {
    val items = initialItems.toMutableList()
    var failOnAdd = false
    var failOnExists = false
    private var nextId = 100L

    override fun getAll(): Flow<List<CollectionItem>> = flowOf(items.toList())

    override fun getByType(type: ItemType): Flow<List<CollectionItem>> = flowOf(items.filter { it.type == type })

    override fun getFavorites(): Flow<List<CollectionItem>> = flowOf(items.filter { it.favorite })

    override suspend fun getById(id: Long): CollectionItem? = items.find { it.id == id }

    override suspend fun existsByTitleAndType(
        title: String,
        type: ItemType,
        excludeId: Long,
    ): Boolean {
        if (failOnExists) throw RuntimeException("exists failed")
        return items.any { it.title == title && it.type == type && it.id != excludeId }
    }

    override suspend fun add(item: CollectionItem): Long {
        if (failOnAdd) throw RuntimeException("add failed")
        val id = nextId++
        items += item.copy(id = id)
        return id
    }

    override suspend fun update(item: CollectionItem): QueryResult<Long> {
        val index = items.indexOfFirst { it.id == item.id }
        if (index >= 0) items[index] = item
        return QueryResult.Value(1L)
    }

    override suspend fun updateLoan(
        id: Long,
        isBorrowed: Boolean,
        borrowedSince: Long?,
        borrowedBy: String?,
    ) = QueryResult.Value(1L)

    override suspend fun updateTotals(
        id: Long,
        totTome: Int?,
        totChapter: Int?,
        totEpisode: Int?,
    ) = QueryResult.Value(1L)

    override suspend fun delete(id: Long) = QueryResult.Value(1L)
}

// ── Tests ──────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var backupRepo: FakeBackupRepository
    private lateinit var collectionRepo: FakeCollectionRepository
    private lateinit var settings: FakeSettings
    private lateinit var userPrefs: UserPreferencesRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        backupRepo = FakeBackupRepository()
        collectionRepo = FakeCollectionRepository(listOf(naruto, dune))
        settings = FakeSettings()
        userPrefs = UserPreferencesRepository(settings)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = SettingsViewModel(backupRepo, collectionRepo, userPrefs)

    // ── Initial state ──────────────────────────────────────────────────────────

    @Test
    fun `initial state uses defaults when nothing is stored`() {
        val vm = buildViewModel()

        assertEquals(ThemeMode.SYSTEM, vm.themeMode.value)
        assertTrue(vm.saveEnglishDescription.value)
        assertTrue(vm.replaceTitle.value)
        assertNull(vm.email.value)
        assertNull(vm.lastBackupDate.value)
        assertFalse(vm.isLoading.value)
        assertNull(vm.notification.value)
        assertNull(vm.restoreConflict.value)
        assertNull(vm.importConflict.value)
    }

    @Test
    fun `initial state is loaded from stored preferences`() {
        userPrefs.themeMode = ThemeMode.DARK
        userPrefs.backupEmail = "me@example.com"
        userPrefs.lastBackupDate = "1 Jan 2026 at 10:00"

        val vm = buildViewModel()

        assertEquals(ThemeMode.DARK, vm.themeMode.value)
        assertEquals("me@example.com", vm.email.value)
        assertEquals("1 Jan 2026 at 10:00", vm.lastBackupDate.value)
    }

    // ── Preferences ────────────────────────────────────────────────────────────

    @Test
    fun `setThemeMode updates flow and persists`() {
        val vm = buildViewModel()

        vm.setThemeMode(ThemeMode.LIGHT)

        assertEquals(ThemeMode.LIGHT, vm.themeMode.value)
        assertEquals(ThemeMode.LIGHT, userPrefs.themeMode)
    }

    @Test
    fun `setSaveEnglishDescription updates flow and persists`() {
        val vm = buildViewModel()

        vm.setSaveEnglishDescription(false)

        assertFalse(vm.saveEnglishDescription.value)
        assertFalse(userPrefs.saveEnglishDescription)
    }

    @Test
    fun `setReplaceTitle updates flow and persists`() {
        val vm = buildViewModel()

        vm.setReplaceTitle(false)

        assertFalse(vm.replaceTitle.value)
        assertFalse(userPrefs.replaceTitle)
    }

    // ── Notifications ──────────────────────────────────────────────────────────

    @Test
    fun `signInFailed posts SignInError and clearNotification resets it`() {
        val vm = buildViewModel()

        vm.signInFailed()
        assertEquals(BackupNotification.SignInError, vm.notification.value)

        vm.clearNotification()
        assertNull(vm.notification.value)
    }

    // ── Sign in / out ──────────────────────────────────────────────────────────

    @Test
    fun `signInCompleted does nothing when no user is signed in`() =
        runTest {
            backupRepo.currentEmail = null
            val vm = buildViewModel()

            vm.signInCompleted()
            advanceUntilIdle()

            assertNull(vm.email.value)
            assertNull(userPrefs.backupEmail)
        }

    @Test
    fun `signInCompleted stores email and fetches last backup date`() =
        runTest {
            backupRepo.currentEmail = "me@example.com"
            backupRepo.lastBackupDate = "2 Feb 2026 at 09:30"
            val vm = buildViewModel()

            vm.signInCompleted()
            assertEquals("me@example.com", vm.email.value)
            advanceUntilIdle()

            assertEquals("me@example.com", userPrefs.backupEmail)
            assertEquals("2 Feb 2026 at 09:30", vm.lastBackupDate.value)
            assertEquals("2 Feb 2026 at 09:30", userPrefs.lastBackupDate)
        }

    @Test
    fun `signInCompleted keeps existing backup date when cloud has none`() =
        runTest {
            userPrefs.lastBackupDate = "old date"
            backupRepo.currentEmail = "me@example.com"
            backupRepo.lastBackupDate = null
            val vm = buildViewModel()

            vm.signInCompleted()
            advanceUntilIdle()

            assertEquals("old date", vm.lastBackupDate.value)
        }

    @Test
    fun `signOut signs out and clears email and backup date`() =
        runTest {
            userPrefs.backupEmail = "me@example.com"
            userPrefs.lastBackupDate = "old date"
            val vm = buildViewModel()

            vm.signOut()
            advanceUntilIdle()

            assertEquals(1, backupRepo.signOutCalls)
            assertNull(vm.email.value)
            assertNull(vm.lastBackupDate.value)
            assertNull(userPrefs.backupEmail)
            assertNull(userPrefs.lastBackupDate)
        }

    // ── backupNow ──────────────────────────────────────────────────────────────

    @Test
    fun `backupNow uploads collection and posts BackupSuccess`() =
        runTest {
            val vm = buildViewModel()

            vm.backupNow()
            advanceUntilIdle()

            assertEquals(listOf(naruto, dune), backupRepo.backedUpItems)
            assertEquals(BackupNotification.BackupSuccess, vm.notification.value)
            assertFalse(vm.isLoading.value)
        }

    @Test
    fun `backupNow stores a formatted last backup date`() =
        runTest {
            val vm = buildViewModel()

            vm.backupNow()
            advanceUntilIdle()

            val date = assertNotNull(vm.lastBackupDate.value)
            assertTrue(
                Regex("""\d{1,2} [A-Z][a-z]{2} \d{4} at \d{2}:\d{2}""").matches(date),
                "Unexpected date format: $date",
            )
            assertEquals(date, userPrefs.lastBackupDate)
        }

    @Test
    fun `backupNow posts BackupError and keeps previous date on failure`() =
        runTest {
            userPrefs.lastBackupDate = "old date"
            backupRepo.shouldFail = true
            val vm = buildViewModel()

            vm.backupNow()
            advanceUntilIdle()

            assertEquals(BackupNotification.BackupError, vm.notification.value)
            assertEquals("old date", vm.lastBackupDate.value)
            assertFalse(vm.isLoading.value)
        }

    @Test
    fun `backupNow sets isLoading while running`() =
        runTest {
            val gate = CompletableDeferred<Unit>()
            backupRepo.gate = gate
            val vm = buildViewModel()

            vm.backupNow()
            advanceUntilIdle()
            assertTrue(vm.isLoading.value)

            gate.complete(Unit)
            advanceUntilIdle()
            assertFalse(vm.isLoading.value)
        }

    // ── deleteCloudBackup ──────────────────────────────────────────────────────

    @Test
    fun `deleteCloudBackup clears backup date and posts DeleteSuccess`() =
        runTest {
            userPrefs.lastBackupDate = "old date"
            val vm = buildViewModel()

            vm.deleteCloudBackup()
            advanceUntilIdle()

            assertEquals(1, backupRepo.deleteCalls)
            assertNull(vm.lastBackupDate.value)
            assertNull(userPrefs.lastBackupDate)
            assertEquals(BackupNotification.DeleteSuccess, vm.notification.value)
            assertFalse(vm.isLoading.value)
        }

    @Test
    fun `deleteCloudBackup posts DeleteError and keeps date on failure`() =
        runTest {
            userPrefs.lastBackupDate = "old date"
            backupRepo.shouldFail = true
            val vm = buildViewModel()

            vm.deleteCloudBackup()
            advanceUntilIdle()

            assertEquals(BackupNotification.DeleteError, vm.notification.value)
            assertEquals("old date", vm.lastBackupDate.value)
            assertFalse(vm.isLoading.value)
        }

    // ── restore ────────────────────────────────────────────────────────────────

    @Test
    fun `restore without duplicates adds all items and posts RestoreSuccess`() =
        runTest {
            backupRepo.remoteItems = listOf(remoteOnePiece)
            val vm = buildViewModel()

            vm.restore()
            advanceUntilIdle()

            assertEquals(3, collectionRepo.items.size)
            assertTrue(collectionRepo.items.any { it.title == "One Piece" })
            assertEquals(BackupNotification.RestoreSuccess, vm.notification.value)
            assertNull(vm.restoreConflict.value)
            assertFalse(vm.isLoading.value)
        }

    @Test
    fun `restore with duplicates raises a conflict and inserts nothing`() =
        runTest {
            backupRepo.remoteItems = listOf(remoteNaruto, remoteOnePiece)
            val vm = buildViewModel()

            vm.restore()
            advanceUntilIdle()

            val conflict = assertNotNull(vm.restoreConflict.value)
            assertEquals(1, conflict.duplicateCount)
            assertEquals(listOf(remoteNaruto, remoteOnePiece), conflict.pendingItems)
            assertEquals(2, collectionRepo.items.size)
            assertNull(vm.notification.value)
            assertNull(vm.importConflict.value)
        }

    @Test
    fun `restore posts RestoreError when the backup fails`() =
        runTest {
            backupRepo.shouldFail = true
            val vm = buildViewModel()

            vm.restore()
            advanceUntilIdle()

            assertEquals(BackupNotification.RestoreError, vm.notification.value)
            assertFalse(vm.isLoading.value)
        }

    @Test
    fun `restore posts RestoreError when inserting fails`() =
        runTest {
            backupRepo.remoteItems = listOf(remoteOnePiece)
            collectionRepo.failOnAdd = true
            val vm = buildViewModel()

            vm.restore()
            advanceUntilIdle()

            assertEquals(BackupNotification.RestoreError, vm.notification.value)
            assertFalse(vm.isLoading.value)
        }

    // ── applyRestore ───────────────────────────────────────────────────────────

    @Test
    fun `applyRestore without a pending conflict does nothing`() =
        runTest {
            val vm = buildViewModel()

            vm.applyRestore(RestoreStrategy.DUPLICATE)
            advanceUntilIdle()

            assertEquals(2, collectionRepo.items.size)
            assertNull(vm.notification.value)
        }

    @Test
    fun `applyRestore DUPLICATE adds every item including duplicates`() =
        runTest {
            backupRepo.remoteItems = listOf(remoteNaruto, remoteOnePiece)
            val vm = buildViewModel()
            vm.restore()
            advanceUntilIdle()

            vm.applyRestore(RestoreStrategy.DUPLICATE)
            assertNull(vm.restoreConflict.value)
            advanceUntilIdle()

            assertEquals(4, collectionRepo.items.size)
            assertEquals(2, collectionRepo.items.count { it.title == "Naruto" })
            assertEquals(BackupNotification.RestoreSuccess, vm.notification.value)
            assertFalse(vm.isLoading.value)
        }

    @Test
    fun `applyRestore SKIP adds only non-duplicate items`() =
        runTest {
            backupRepo.remoteItems = listOf(remoteNaruto, remoteOnePiece)
            val vm = buildViewModel()
            vm.restore()
            advanceUntilIdle()

            vm.applyRestore(RestoreStrategy.SKIP)
            advanceUntilIdle()

            assertEquals(3, collectionRepo.items.size)
            assertEquals(1, collectionRepo.items.count { it.title == "Naruto" })
            assertEquals(1, collectionRepo.items.first { it.title == "Naruto" }.tome)
            assertTrue(collectionRepo.items.any { it.title == "One Piece" })
            assertEquals(BackupNotification.RestoreSuccess, vm.notification.value)
        }

    @Test
    fun `applyRestore REPLACE updates the existing item and adds new ones`() =
        runTest {
            backupRepo.remoteItems = listOf(remoteNaruto, remoteOnePiece)
            val vm = buildViewModel()
            vm.restore()
            advanceUntilIdle()

            vm.applyRestore(RestoreStrategy.REPLACE)
            advanceUntilIdle()

            assertEquals(3, collectionRepo.items.size)
            val replaced = collectionRepo.items.first { it.title == "Naruto" }
            assertEquals(naruto.id, replaced.id)
            assertEquals(5, replaced.tome)
            assertTrue(collectionRepo.items.any { it.title == "One Piece" })
            assertEquals(BackupNotification.RestoreSuccess, vm.notification.value)
        }

    @Test
    fun `applyRestore posts RestoreError when inserting fails`() =
        runTest {
            backupRepo.remoteItems = listOf(remoteNaruto)
            val vm = buildViewModel()
            vm.restore()
            advanceUntilIdle()
            collectionRepo.failOnAdd = true

            vm.applyRestore(RestoreStrategy.DUPLICATE)
            advanceUntilIdle()

            assertEquals(BackupNotification.RestoreError, vm.notification.value)
            assertFalse(vm.isLoading.value)
        }

    @Test
    fun `dismissRestoreConflict clears the conflict without inserting`() =
        runTest {
            backupRepo.remoteItems = listOf(remoteNaruto)
            val vm = buildViewModel()
            vm.restore()
            advanceUntilIdle()

            vm.dismissRestoreConflict()

            assertNull(vm.restoreConflict.value)
            assertEquals(2, collectionRepo.items.size)
        }

    // ── importItems ────────────────────────────────────────────────────────────

    @Test
    fun `importItems without duplicates adds items and posts RestoreSuccess`() =
        runTest {
            val vm = buildViewModel()

            vm.importItems(listOf(remoteOnePiece))
            advanceUntilIdle()

            assertEquals(3, collectionRepo.items.size)
            assertEquals(BackupNotification.RestoreSuccess, vm.notification.value)
            assertNull(vm.importConflict.value)
            assertFalse(vm.isLoading.value)
        }

    @Test
    fun `importItems with duplicates raises an import conflict only`() =
        runTest {
            val vm = buildViewModel()

            vm.importItems(listOf(remoteNaruto, remoteOnePiece))
            advanceUntilIdle()

            val conflict = assertNotNull(vm.importConflict.value)
            assertEquals(1, conflict.duplicateCount)
            assertNull(vm.restoreConflict.value)
            assertEquals(2, collectionRepo.items.size)
        }

    @Test
    fun `importItems posts RestoreError on failure`() =
        runTest {
            collectionRepo.failOnExists = true
            val vm = buildViewModel()

            vm.importItems(listOf(remoteOnePiece))
            advanceUntilIdle()

            assertEquals(BackupNotification.RestoreError, vm.notification.value)
            assertFalse(vm.isLoading.value)
        }

    @Test
    fun `applyImport SKIP resolves the import conflict`() =
        runTest {
            val vm = buildViewModel()
            vm.importItems(listOf(remoteNaruto, remoteOnePiece))
            advanceUntilIdle()

            vm.applyImport(RestoreStrategy.SKIP)
            assertNull(vm.importConflict.value)
            advanceUntilIdle()

            assertEquals(3, collectionRepo.items.size)
            assertEquals(1, collectionRepo.items.first { it.title == "Naruto" }.tome)
            assertEquals(BackupNotification.RestoreSuccess, vm.notification.value)
        }

    @Test
    fun `applyImport does not touch a pending restore conflict`() =
        runTest {
            backupRepo.remoteItems = listOf(remoteNaruto)
            val vm = buildViewModel()
            vm.restore()
            advanceUntilIdle()

            vm.applyImport(RestoreStrategy.DUPLICATE)
            advanceUntilIdle()

            assertNotNull(vm.restoreConflict.value)
            assertEquals(2, collectionRepo.items.size)
        }

    @Test
    fun `dismissImportConflict clears the conflict without inserting`() =
        runTest {
            val vm = buildViewModel()
            vm.importItems(listOf(remoteNaruto))
            advanceUntilIdle()

            vm.dismissImportConflict()

            assertNull(vm.importConflict.value)
            assertEquals(2, collectionRepo.items.size)
        }
}
