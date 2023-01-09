package com.atiga.cakeorder.ui.user

import androidx.lifecycle.*
import com.atiga.cakeorder.core.domain.model.user.TokenUser
import com.atiga.cakeorder.core.domain.model.user.User
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.Resource
import com.atiga.cakeorder.core.network.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var userData : LiveData<Resource<List<User>>> = MutableLiveData()
    var refreshToken = MutableLiveData<LoginResponse>()
    var error = MutableLiveData<String>()

    suspend fun getAllUser(token: String){
        userData = liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getAllUser(token)))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }

    suspend fun refreshToken(token: String) {
        viewModelScope.launch {
            try {
                refreshToken.value = cakeUseCase.refreshToken(TokenUser(token))
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }
}