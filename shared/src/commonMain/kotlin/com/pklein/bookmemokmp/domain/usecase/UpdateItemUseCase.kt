package com.pklein.bookmemokmp.domain.usecase

import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.repository.ICollectionRepository

class UpdateItemUseCase(
    private val repository: ICollectionRepository,
) {
    suspend operator fun invoke(item: CollectionItem) = repository.update(item)
}
