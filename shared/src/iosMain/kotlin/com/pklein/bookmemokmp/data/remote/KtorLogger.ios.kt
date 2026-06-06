package com.pklein.bookmemokmp.data.remote

import io.ktor.client.plugins.logging.Logger

actual fun platformKtorLogger(): Logger = object : Logger {
    override fun log(message: String) {
        println(message)
    }
}