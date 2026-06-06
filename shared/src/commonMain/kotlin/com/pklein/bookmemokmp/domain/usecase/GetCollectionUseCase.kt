package com.pklein.bookmemokmp.domain.usecase

import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow

class GetCollectionUseCase(
    private val repository: CollectionRepository,
) {
    fun all(): Flow<List<CollectionItem>> = repository.getAll()

    fun byType(type: ItemType): Flow<List<CollectionItem>> = repository.getByType(type)

    suspend fun getById(id: Long): CollectionItem? = repository.getById(id)

    suspend fun existsByTitleAndType(
        title: String,
        type: ItemType,
        excludeId: Long,
    ): Boolean = repository.existsByTitleAndType(title, type, excludeId)

    suspend fun updateTotals(
        id: Long,
        totTome: Int?,
        totChapter: Int?,
        totEpisode: Int?,
    ) = repository.updateTotals(id, totTome, totChapter, totEpisode)
}
