package com.atiga.cakeorder.ui.masterdata.category

import androidx.lifecycle.*
import com.atiga.cakeorder.core.domain.model.DeleteItem
import com.atiga.cakeorder.core.domain.model.category.AddCategory
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EditCategoryViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var detail: LiveData<Resource<Category>> = MutableLiveData()
    var isDeleted = MutableLiveData<DeleteItem>()
    var editedCategory = MutableLiveData<Category>()
    var addedCategory = MutableLiveData<Category>()
    var error = MutableLiveData<String>()

    suspend fun addCategory(data: AddCategory) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                addedCategory.value = cakeUseCase.addCategory(data)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun getCategoryDetail(id: Int){
        detail = liveData(Dispatchers.IO){
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getCategoryById(id)))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }

    suspend fun updateCategory(id: Int, data: Category){
        viewModelScope.launch {
            try {
                editedCategory.value = cakeUseCase.editCategory(id, data)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun deleteCategory(id: Int){
        viewModelScope.launch {
            try {
                isDeleted.value = cakeUseCase.deleteCategory(id)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }


}