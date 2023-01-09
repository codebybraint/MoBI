package com.atiga.cakeorder.ui.user.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atiga.cakeorder.core.domain.model.user.CreateUser
import com.atiga.cakeorder.core.domain.model.user.LoginUser
import com.atiga.cakeorder.core.domain.model.user.UserRole
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AddUserViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var addedUser = MutableLiveData<LoginUser>()
    var error = MutableLiveData<String>()
    var userRoles = MutableLiveData<List<UserRole>>()

    suspend fun addUser(username: String, password: String, roleId: Int, token: String) {
        viewModelScope.launch {
            try {
                addedUser.value = cakeUseCase.addUser(CreateUser(username,password,roleId),token)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun getUserRoles(){
        viewModelScope.launch {
            try {
                userRoles.value = cakeUseCase.getUserRoles()
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

}