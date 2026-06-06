package com.pklein.bookmemokmp.di

import org.koin.core.module.Module

expect fun platformModule(): Module

fun commonModules() = listOf(
    platformModule(),
    dataModule,
    domainModule,
    presentationModule
)
