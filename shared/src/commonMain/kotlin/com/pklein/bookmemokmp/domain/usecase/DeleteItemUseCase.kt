package com.pklein.bookmemokmp.domain.usecase

import com.pklein.bookmemokmp.domain.repository.ICollectionRepository

class DeleteItemUseCase(
    private val repository: ICollectionRepository,
) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}
