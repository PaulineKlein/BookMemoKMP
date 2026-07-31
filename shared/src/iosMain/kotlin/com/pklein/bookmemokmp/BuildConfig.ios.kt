package com.pklein.bookmemokmp

import platform.Foundation.NSBundle

actual fun isDebugBuild(): Boolean = false

actual fun googleBooksApiKey(): String? = null

actual fun mangaApiKey(): String? = null

actual fun androidPackageName(): String? = null

actual fun androidCertFingerprint(): String? = null

actual fun appVersion(): String? =
    NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String