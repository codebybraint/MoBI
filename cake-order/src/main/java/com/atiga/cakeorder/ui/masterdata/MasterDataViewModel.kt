package com.atiga.cakeorder.ui.masterdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.Resource
import kotlinx.coroutines.Dispatchers

class MasterDataViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var categoryData: LiveData<Resource<List<Category>>> = MutableLiveData()

    suspend fun getCategories(){
        categoryData = liveData(Dispatchers.IO){
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getAllCategory()))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }

}