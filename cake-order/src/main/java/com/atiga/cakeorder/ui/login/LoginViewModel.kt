package com.atiga.cakeorder.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atiga.cakeorder.core.domain.model.user.LoginUser
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.response.LoginResponse

class LoginViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var login = MutableLiveData<LoginResponse>()
    var loginFailed = MutableLiveData<String>()

    suspend fun login(username: String, password: String) {
        try {
            login.value = cakeUseCase.login(LoginUser(username, password))
        } catch (ex: Exception){
            loginFailed.value = ex.message
        }
    }
}