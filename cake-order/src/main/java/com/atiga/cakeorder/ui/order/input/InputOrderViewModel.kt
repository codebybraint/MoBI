package com.atiga.cakeorder.ui.order.input

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.atiga.cakeorder.core.domain.model.order.AddOrder
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

@FlowPreview
@ExperimentalCoroutinesApi
class InputOrderViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var addedOrder = MutableLiveData<Order>()
    val nameQueryChannel = BroadcastChannel<String>(Channel.CONFLATED)
    val phoneQueryChannel = BroadcastChannel<String>(Channel.CONFLATED)
    var error = MutableLiveData<String>()

    //execute add order
    suspend fun addOrder(userKey: String, data: AddOrder){
        viewModelScope.launch {
            try {
                addedOrder.value = cakeUseCase.addOrder(userKey, data)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    //search name from query
    val nameSearchResult = nameQueryChannel.asFlow()
        .debounce(300)
        .distinctUntilChanged()
        .filter {
            it.trim().isNotBlank()
        }
        .mapLatest {
            cakeUseCase.getCustomer().filter { x -> x.name.contains(it) }
        }
        .asLiveData()

    //search phone number from query
    val phoneSearchResult = phoneQueryChannel.asFlow()
        .debounce(300)
        .distinctUntilChanged()
        .filter {
            it.trim().isNotBlank()
        }
        .mapLatest {
            cakeUseCase.getCustomer().filter { x -> x.phoneNumber.contains(it) }
        }
        .asLiveData()

}