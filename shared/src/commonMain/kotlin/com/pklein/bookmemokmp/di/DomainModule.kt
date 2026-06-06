package com.pklein.bookmemokmp.di

import com.pklein.bookmemokmp.domain.usecase.AddItemUseCase
import com.pklein.bookmemokmp.domain.usecase.BookSearchUseCase
import com.pklein.bookmemokmp.domain.usecase.DeleteItemUseCase
import com.pklein.bookmemokmp.domain.usecase.GetCollectionUseCase
import com.pklein.bookmemokmp.domain.usecase.UpdateItemUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetCollectionUseCase)
    factoryOf(::AddItemUseCase)
    factoryOf(::UpdateItemUseCase)
    factoryOf(::DeleteItemUseCase)
    factoryOf(::BookSearchUseCase)
}
