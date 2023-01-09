package com.atiga.cakeorder.kitchen.di

import com.atiga.cakeorder.core.domain.usecase.CakeInteractor
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.kitchen.ui.detail.DetailViewModel
import com.atiga.cakeorder.kitchen.ui.main.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<CakeUseCase> { CakeInteractor(get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { DetailViewModel(get()) }
}