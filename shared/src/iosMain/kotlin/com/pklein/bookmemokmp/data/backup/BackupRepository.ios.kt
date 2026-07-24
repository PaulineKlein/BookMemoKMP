package com.pklein.bookmemokmp.data.backup

import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.repository.IBackupRepository

class BackupRepository : IBackupRepository {
    override fun getCurrentUserEmail(): String? = null

    override suspend fun signIn(webClientId: String) = Unit

    override suspend fun signOut() = Unit

    override suspend fun fetchLastBackupDate(): String? = null

    override suspend fun backup(items: List<CollectionItem>) = Unit

    override suspend fun restore(): List<CollectionItem> = emptyList()
}
