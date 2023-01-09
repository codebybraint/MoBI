package com.atiga.cakeorder.di

import com.atiga.cakeorder.core.domain.usecase.CakeInteractor
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.ui.capacity.CapacityViewModel
import com.atiga.cakeorder.ui.login.LoginViewModel
import com.atiga.cakeorder.ui.masterdata.MasterDataViewModel
import com.atiga.cakeorder.ui.masterdata.category.EditCategoryViewModel
import com.atiga.cakeorder.ui.masterdata.product.AddProductViewModel
import com.atiga.cakeorder.ui.masterdata.product.ListProductFromHomeViewModel
import com.atiga.cakeorder.ui.masterdata.product.ListProductViewModel
import com.atiga.cakeorder.ui.masterdata.subcategory.AddSubCategoryViewModel
import com.atiga.cakeorder.ui.masterdata.subcategory.EditSubCategoryViewModel
import com.atiga.cakeorder.ui.order.OrderViewModel
import com.atiga.cakeorder.ui.order.detail.DetailOrderViewModel
import com.atiga.cakeorder.ui.order.edit.EditOrderViewModel
import com.atiga.cakeorder.ui.order.input.InputOrderViewModel
import com.atiga.cakeorder.ui.order.input.orderitem.AddItemViewModel
import com.atiga.cakeorder.ui.report.ReportViewModel
import com.atiga.cakeorder.ui.report.detail.DetailReportViewModel
import com.atiga.cakeorder.ui.report.track.TrackOrderViewModel
import com.atiga.cakeorder.ui.report.track.detail.DetailTrackOrderViewModel
import com.atiga.cakeorder.ui.user.UserViewModel
import com.atiga.cakeorder.ui.user.add.AddUserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<CakeUseCase> { CakeInteractor(get()) }
}

@ExperimentalCoroutinesApi
@FlowPreview
val viewModelModule = module {
    viewModel { MasterDataViewModel(get()) }
    viewModel { OrderViewModel(get()) }
    viewModel { ReportViewModel(get()) }
    viewModel { CapacityViewModel(get()) }
    viewModel { EditCategoryViewModel(get()) }
    viewModel { AddSubCategoryViewModel(get()) }
    viewModel { EditSubCategoryViewModel(get()) }
    viewModel { ListProductViewModel(get()) }
    viewModel { AddProductViewModel(get()) }
    viewModel { AddItemViewModel(get()) }
    viewModel { InputOrderViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { AddUserViewModel(get()) }
    viewModel { EditOrderViewModel(get()) }
    viewModel { DetailOrderViewModel(get()) }
    viewModel { DetailReportViewModel(get()) }
    viewModel { DetailTrackOrderViewModel(get()) }
    viewModel { TrackOrderViewModel(get()) }
    viewModel { ListProductFromHomeViewModel(get()) }
}