package com.atiga.cakeorder.kitchen.ui.main

import androidx.lifecycle.*
import com.atiga.cakeorder.core.domain.model.user.LoginUser
import com.atiga.cakeorder.core.domain.model.user.User
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.Resource
import com.atiga.cakeorder.core.network.response.KitchenOrderResponse
import com.atiga.cakeorder.core.network.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val cakeUseCase: CakeUseCase) : ViewModel(){
    var orderData : LiveData<Resource<KitchenOrderResponse>> = MutableLiveData()
    var userData = MutableLiveData<List<User>>()
    var error = MutableLiveData<String>()
    var login = MutableLiveData<LoginResponse>()
    var loginFailed = MutableLiveData<String>()

    suspend fun getOrderToWork(fIsStarted: Boolean? = null,
                               fDate: String? = null,
                               fUser: String? = null,
                               fJobStartDate: String? = null,
                               fJobEndDate: String? = null){
        orderData = liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getOrderToWork(fIsStarted, fDate, fUser, fJobStartDate, fJobEndDate)))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }

    suspend fun getAllUser(token: String){
        viewModelScope.launch {
            try {
                userData.value = cakeUseCase.getAllUser(token)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun login(username: String, password: String) {
        try {
            login.value = cakeUseCase.login(LoginUser(username, password))
        } catch (ex: Exception){
            loginFailed.value = ex.message
        }
    }
}