package com.pklein.bookmemokmp.domain.repository

import com.pklein.bookmemokmp.domain.model.CollectionItem

interface IBackupRepository {
    fun getCurrentUserEmail(): String?

    suspend fun signIn(webClientId: String)

    suspend fun signOut()

    suspend fun fetchLastBackupDate(): String?

    suspend fun backup(items: List<CollectionItem>)

    suspend fun restore(): List<CollectionItem>
}
