package com.pklein.bookmemokmp.domain.usecase

import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.repository.CollectionRepository

class AddItemUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(item: CollectionItem): Long = repository.add(item)
}
