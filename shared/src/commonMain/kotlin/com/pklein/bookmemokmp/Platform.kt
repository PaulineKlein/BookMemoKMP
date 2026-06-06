package com.pklein.bookmemokmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

val isAndroidPlatform: Boolean get() = getPlatform().name.startsWith("Android")
