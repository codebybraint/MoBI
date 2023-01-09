package com.atiga.cakeorder.ui.report.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.core.domain.model.report.Report
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.domain.usecase.CakeUseCase
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailReportViewModel(private val cakeUseCase: CakeUseCase) : ViewModel() {
    var order = MutableLiveData<List<Order>>()
    var category = MutableLiveData<List<Category>>()
    var subCategory = MutableLiveData<List<SubCategory>>()
    var error = MutableLiveData<String>()

    suspend fun getReport(reportType: Int, data: Report){
        viewModelScope.launch {
            try {
                order.value = cakeUseCase.getReport(reportType, data)
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