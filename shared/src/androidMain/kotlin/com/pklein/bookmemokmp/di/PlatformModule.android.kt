package com.pklein.bookmemokmp.di

import com.pklein.bookmemokmp.data.DatabaseDriverFactory
import com.pklein.bookmemokmp.data.backup.BackupRepository
import com.pklein.bookmemokmp.domain.repository.IBackupRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module =
    module {
        single { DatabaseDriverFactory(androidContext()) }
        single { BackupRepository(androidContext()) } bind IBackupRepository::class
    }
