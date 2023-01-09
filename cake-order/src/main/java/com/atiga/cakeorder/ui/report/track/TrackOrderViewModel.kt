package com.atiga.cakeorder.ui.report.track

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TrackOrderViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var orderResult = MutableLiveData<List<Order>>()
    var error = MutableLiveData<String>()

    suspend fun getOrderByNameAndOrPhone(startDate: String, endDate: String, name: String, phone: String) {
        viewModelScope.launch {
            try {
                orderResult.value = cakeUseCase.getOrderByNameAndOrPhone(startDate, endDate, name, phone)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

}