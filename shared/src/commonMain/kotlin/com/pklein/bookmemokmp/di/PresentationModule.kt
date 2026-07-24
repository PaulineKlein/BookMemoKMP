package com.pklein.bookmemokmp.di

import com.pklein.bookmemokmp.presentation.additem.viewmodel.AddItemViewModel
import com.pklein.bookmemokmp.presentation.collection.viewmodel.CollectionViewModel
import com.pklein.bookmemokmp.presentation.settings.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule =
    module {
        viewModelOf(::CollectionViewModel)
        viewModelOf(::AddItemViewModel)
        viewModelOf(::SettingsViewModel)
    }
