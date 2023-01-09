package com.atiga.cakeorder.ui.order.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atiga.cakeorder.core.domain.model.order.FinishOrder
import com.atiga.cakeorder.core.domain.model.order.FinishingOrder
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailOrderViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var detailOrder = MutableLiveData<Order>()
    var finishOrder = MutableLiveData<FinishOrder>()
    var error = MutableLiveData<String>()

    suspend fun getOrderDetail(id: String) {
        viewModelScope.launch {
            try {
                detailOrder.value = cakeUseCase.getOrderById(id)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun finishOrder(orderNumber: String, itemKey: Int) {
        viewModelScope.launch {
            try {
                finishOrder.value = cakeUseCase.finishOrder(
                    orderNumber,
                    itemKey,
                    FinishingOrder(
                        itemKey
                    )
                )
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }
}