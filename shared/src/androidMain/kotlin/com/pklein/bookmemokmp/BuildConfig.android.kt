package com.pklein.bookmemokmp

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest
import org.koin.java.KoinJavaComponent.getKoin

actual fun isDebugBuild(): Boolean =
    getKoin().get<Application>().applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

actual fun googleBooksApiKey(): String? =
    com.pklein.bookmemokmp.shared.BuildConfig.GOOGLE_BOOKS_API_KEY.takeIf { it.isNotEmpty() }

actual fun mangaApiKey(): String? =
    com.pklein.bookmemokmp.shared.BuildConfig.MANGA_API_KEY.takeIf { it.isNotEmpty() }

actual fun androidPackageName(): String? {
    val app = getKoin().get<Application>()
    return app.packageName
}

actual fun androidCertFingerprint(): String? {
    val app = getKoin().get<Application>()
    return try {
        val sig = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            app.packageManager
                .getPackageInfo(app.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                .signingInfo
                ?.apkContentsSigners
                ?.firstOrNull()
        } else {
            @Suppress("DEPRECATION")
            app.packageManager
                .getPackageInfo(app.packageName, PackageManager.GET_SIGNATURES)
                .signatures
                ?.firstOrNull()
        }
        sig?.let {
            MessageDigest.getInstance("SHA1")
                .digest(it.toByteArray())
                .joinToString("") { b -> "%02x".format(b) }
        }
    } catch (_: Exception) {
        null
    }
}