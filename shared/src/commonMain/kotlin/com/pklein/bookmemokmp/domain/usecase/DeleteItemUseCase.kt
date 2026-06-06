package com.pklein.bookmemokmp.domain.usecase

import com.pklein.bookmemokmp.domain.repository.CollectionRepository

class DeleteItemUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}
