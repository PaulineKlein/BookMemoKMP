package com.pklein.bookmemokmp.data.remote

import android.util.Log
import io.ktor.client.plugins.logging.Logger

actual fun platformKtorLogger(): Logger = object : Logger {
    override fun log(message: String) {
        Log.d("Ktor", message)
    }
}