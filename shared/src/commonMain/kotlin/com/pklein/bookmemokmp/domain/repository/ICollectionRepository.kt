package com.pklein.bookmemokmp.domain.repository

import app.cash.sqldelight.db.QueryResult
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import kotlinx.coroutines.flow.Flow

interface ICollectionRepository {
    fun getAll(): Flow<List<CollectionItem>>

    fun getByType(type: ItemType): Flow<List<CollectionItem>>

    fun getFavorites(): Flow<List<CollectionItem>>

    suspend fun getById(id: Long): CollectionItem?

    suspend fun existsByTitleAndType(
        title: String,
        type: ItemType,
        excludeId: Long,
    ): Boolean

    suspend fun add(item: CollectionItem): Long

    suspend fun update(item: CollectionItem): QueryResult<Long>

    suspend fun updateLoan(
        id: Long,
        isBorrowed: Boolean,
        borrowedSince: Long?,
        borrowedBy: String?,
    ): QueryResult<Long>

    suspend fun updateTotals(
        id: Long,
        totTome: Int?,
        totChapter: Int?,
        totEpisode: Int?,
    ): QueryResult<Long>

    suspend fun delete(id: Long): QueryResult<Long>
}
