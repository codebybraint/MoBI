package com.atiga.cakeorder.ui.capacity

import androidx.lifecycle.*
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategoryCapacity
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CapacityViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var subCategoryData: LiveData<Resource<List<SubCategory>>> = MutableLiveData()
    var editCapacity = MutableLiveData<SubCategory>()
    var error = MutableLiveData<String>()

    suspend fun getAllSubCategory(){
        subCategoryData = liveData(Dispatchers.IO){
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getAllSubCategory()))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }

    suspend fun updateCapacity(id: Int, maxOrder: Int){
        viewModelScope.launch {
            try {
                editCapacity.value = cakeUseCase.updateCapacity(id, SubCategoryCapacity(id, maxOrder))
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }
}