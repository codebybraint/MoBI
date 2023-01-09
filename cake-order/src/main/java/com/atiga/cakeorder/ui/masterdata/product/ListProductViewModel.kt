package com.atiga.cakeorder.ui.masterdata.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import com.atiga.cakeorder.core.network.Resource
import kotlinx.coroutines.Dispatchers

class ListProductViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var subCategory: LiveData<Resource<SubCategory>> = MutableLiveData()
    var products: LiveData<Resource<List<Product>>> = MutableLiveData()

    suspend fun getSubCategory(id: Int){
        subCategory = liveData(Dispatchers.IO){
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getSubCategoryById(id)))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }

    suspend fun getProduct(id: Int){
        products = liveData(Dispatchers.IO){
            emit(Resource.loading(data = null))
            try{
                emit(Resource.success(data = cakeUseCase.getProductBySubCategory(id)))
            } catch (ex: Exception){
                emit(Resource.error(data = null, message = ex.message ?: "Terjadi kesalahan"))
            }
        }
    }
}