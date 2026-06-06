package com.pklein.bookmemokmp.di

import com.pklein.bookmemokmp.data.DatabaseDriverFactory
import com.pklein.bookmemokmp.data.UserPreferencesRepository
import com.pklein.bookmemokmp.data.remote.BookSearchService
import com.pklein.bookmemokmp.data.repository.CollectionRepositoryImpl
import com.pklein.bookmemokmp.database.BookDatabase
import com.pklein.bookmemokmp.domain.repository.BookSearchRepository
import com.pklein.bookmemokmp.domain.repository.CollectionRepository
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { BookDatabase(get<DatabaseDriverFactory>().createDriver()) }
    singleOf(::CollectionRepositoryImpl) bind CollectionRepository::class
    singleOf(::BookSearchRepository)
    singleOf(::BookSearchService)
    single<Settings> { Settings() }
    singleOf(::UserPreferencesRepository)
}
