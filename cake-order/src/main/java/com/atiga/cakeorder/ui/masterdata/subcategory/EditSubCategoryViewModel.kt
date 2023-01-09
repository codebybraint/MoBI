package com.atiga.cakeorder.ui.masterdata.subcategory

import androidx.lifecycle.*
import com.atiga.cakeorder.core.domain.model.DeleteItem
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.model.subcategory.EditSubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.Resource
import com.atiga.cakeorder.core.network.response.EditSubCategoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EditSubCategoryViewModel(private val cakeUseCase: CakeUseCase) : ViewModel()  {
    var detailSubCategory: LiveData<Resource<SubCategory>> = MutableLiveData()
    var category: LiveData<Resource<Category>> = MutableLiveData()
    var isDeleted = MutableLiveData<DeleteItem>()
    var editedSubCategory = MutableLiveData<EditSubCategory>()
    var error = MutableLiveData<String>()

    suspend fun getSubCategoryDetail(id: Int){
        detailSubCategory = liveData(Dispatchers.IO){
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getSubCategoryById(id)))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }

    suspend fun updateSubCategory(id: Int, data: EditSubCategoryResponse){
        viewModelScope.launch {
            try {
                editedSubCategory.value = cakeUseCase.editSubCategory(id, data)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun deleteSubCategory(id: Int){
        viewModelScope.launch {
            try {
                isDeleted.value = cakeUseCase.deleteSubCategory(id)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun getCategory(id: Int){
        category = liveData(Dispatchers.IO){
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getCategoryById(id)))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }
}