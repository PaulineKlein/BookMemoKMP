package com.pklein.bookmemokmp

import android.app.Application
import android.content.pm.ApplicationInfo
import org.koin.java.KoinJavaComponent.getKoin

actual fun isDebugBuild(): Boolean =
    getKoin().get<Application>().applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

actual fun googleBooksApiKey(): String? =
    com.pklein.bookmemokmp.shared.BuildConfig.GOOGLE_BOOKS_API_KEY.takeIf { it.isNotEmpty() }