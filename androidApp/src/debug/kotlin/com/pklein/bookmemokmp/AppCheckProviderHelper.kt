package com.pklein.bookmemokmp

import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

object AppCheckProviderHelper {
    fun factory(): AppCheckProviderFactory = DebugAppCheckProviderFactory.getInstance()
}