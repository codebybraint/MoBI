package com.atiga.cakeorder.kitchen.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atiga.cakeorder.core.domain.model.dekor.JobDekor
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var jobDekorStarted = MutableLiveData<JobDekor>()
    var jobDekorFinished = MutableLiveData<JobDekor>()
    var error = MutableLiveData<String>()

    suspend fun startDekor(orderNumber: String, itemKey: Int) {
        viewModelScope.launch {
            try {
                jobDekorStarted.value = cakeUseCase.startJobDekor(orderNumber, itemKey)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun finishDekor(userKey: String, orderNumber: String, itemKey: Int, urutKey: Int) {
        viewModelScope.launch {
            try {
                jobDekorFinished.value = cakeUseCase.finishJobDekor(userKey, orderNumber, itemKey, urutKey)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }
}