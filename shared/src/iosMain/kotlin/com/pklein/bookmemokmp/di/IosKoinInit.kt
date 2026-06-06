package com.pklein.bookmemokmp.di

import org.koin.core.context.startKoin

fun initKoin() = startKoin {
    modules(commonModules())
}
