package com.atiga.cakeorder.ui.masterdata.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atiga.cakeorder.core.domain.model.DeleteItem
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.model.product.AddProduct
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AddProductViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var addedProduct = MutableLiveData<Product>()
    var editedProduct = MutableLiveData<Product>()
    var isDeleted = MutableLiveData<DeleteItem>()
    var isImageDeleted = MutableLiveData<DeleteItem>()
    var error = MutableLiveData<String>()
    var category = MutableLiveData<List<Category>>()
    var subCategory = MutableLiveData<List<SubCategory>>()

    suspend fun addProduct(data: AddProduct){
        viewModelScope.launch {
            try {
                addedProduct.value = cakeUseCase.addProduct(data)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }
    
    suspend fun editProduct(id: Int, data: Product){
        viewModelScope.launch {
            try {
                editedProduct.value = cakeUseCase.editProduct(id, data)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun deleteProduct(id: Int){
        viewModelScope.launch {
            try {
                isDeleted.value = cakeUseCase.deleteProduct(id)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun deleteProductImage(id: Int){
        viewModelScope.launch {
            try {
                isImageDeleted.value = cakeUseCase.deleteProductImage(id)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun getCategory(){
        viewModelScope.launch {
            try {
                category.value = cakeUseCase.getAllCategory()
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

    suspend fun getSubCategoryByCategoryId(id: Int) {
        viewModelScope.launch {
            try {
                subCategory.value = cakeUseCase.getSubCategoriesByCategoryId(id)
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                errorBody?.let { error.value = it }
            }
        }
    }

}