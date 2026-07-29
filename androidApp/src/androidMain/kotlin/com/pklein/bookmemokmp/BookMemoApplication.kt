package com.pklein.bookmemokmp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.pklein.bookmemokmp.di.commonModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BookMemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.appCheck.installAppCheckProviderFactory(AppCheckProviderHelper.factory())
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BookMemoApplication)
            modules(commonModules())
        }
    }
}
