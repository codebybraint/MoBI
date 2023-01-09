package com.atiga.cakeorder.ui.masterdata.subcategory

import androidx.lifecycle.*
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.model.subcategory.AddSubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AddSubCategoryViewModel(private val cakeUseCase: CakeUseCase) : ViewModel()  {
    var categoryDetail: LiveData<Resource<Category>> = MutableLiveData()
    var subCategoryData: LiveData<Resource<List<SubCategory>>> = MutableLiveData()
    var addedSubCategory = MutableLiveData<SubCategory>()
    var error = MutableLiveData<String>()

    suspend fun getCategoryDetail(id: Int){
        categoryDetail = liveData(Dispatchers.IO){
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getCategoryById(id)))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }

    suspend fun getSubCategoryById(id: Int){
        subCategoryData = liveData(Dispatchers.IO){
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getSubCategoriesByCategoryId(id)))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }

    suspend fun addSubCategory(id: Int, data: AddSubCategory){
        viewModelScope.launch(Dispatchers.Main) {
            try {
                addedSubCategory.value = cakeUseCase.addSubCategory(id, data)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }
}