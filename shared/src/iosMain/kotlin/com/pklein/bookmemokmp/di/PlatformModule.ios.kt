package com.pklein.bookmemokmp.di

import com.pklein.bookmemokmp.data.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory() }
}
