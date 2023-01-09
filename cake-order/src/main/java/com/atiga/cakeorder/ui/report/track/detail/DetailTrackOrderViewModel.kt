package com.atiga.cakeorder.ui.report.track.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atiga.cakeorder.core.domain.model.track.Tracking
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailTrackOrderViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var trackedOrder = MutableLiveData<List<Tracking>>()
    var error = MutableLiveData<String>()

    suspend fun trackOrder(orderNumber: String, itemKey: Int) {
        viewModelScope.launch {
            try {
                trackedOrder.value = cakeUseCase.trackOrder(orderNumber, itemKey)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }
}