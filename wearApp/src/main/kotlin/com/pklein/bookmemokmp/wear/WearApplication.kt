package com.pklein.bookmemokmp.wear

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class WearApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WearApplication)
            modules(
                module {
                    single { WearFavoritesRepository(androidContext()) }
                },
            )
        }
    }
}