package com.atiga.cakeorder.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.Resource
import kotlinx.coroutines.Dispatchers

class OrderViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var orderData : LiveData<Resource<List<Order>>> = MutableLiveData()

    suspend fun getUnfinishedOrder(){
        orderData = liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getUnfinishedOrder()))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
        Log.d("orderData",orderData.toString())
    }

}