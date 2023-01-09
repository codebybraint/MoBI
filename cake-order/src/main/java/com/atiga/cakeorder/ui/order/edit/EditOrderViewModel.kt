package com.atiga.cakeorder.ui.order.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atiga.cakeorder.core.domain.model.order.CancelOrder
import com.atiga.cakeorder.core.domain.model.order.EditOrderItem
import com.atiga.cakeorder.core.domain.model.order.ItemOrder
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import kotlinx.coroutines.launch
import retrofit2.HttpException


class EditOrderViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var orderEdited = MutableLiveData<ItemOrder>()
    var detailOrder = MutableLiveData<Order>()
    var error = MutableLiveData<String>()
    var cancelOrder = MutableLiveData<Order>()

    suspend fun editOrder(orderNumber: String, itemKey: Int, data: EditOrderItem) {
        viewModelScope.launch {
            try {
                orderEdited.value = cakeUseCase.editOrder(orderNumber, itemKey, data)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

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

    suspend fun cancelOrder(data: CancelOrder, userKey: String) {
        viewModelScope.launch {
            try {
                cancelOrder.value = cakeUseCase.cancelOrder(data.noPesanan, data.itemKey, data, userKey)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }
}