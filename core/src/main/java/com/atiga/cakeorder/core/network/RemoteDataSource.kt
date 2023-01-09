package com.atiga.cakeorder.core.network

import com.atiga.cakeorder.core.domain.model.category.AddCategory
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.model.order.AddOrder
import com.atiga.cakeorder.core.domain.model.order.CancelOrder
import com.atiga.cakeorder.core.domain.model.order.EditOrderItem
import com.atiga.cakeorder.core.domain.model.order.FinishingOrder
import com.atiga.cakeorder.core.domain.model.product.AddProduct
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.core.domain.model.report.Report
import com.atiga.cakeorder.core.domain.model.subcategory.AddSubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategoryCapacity
import com.atiga.cakeorder.core.domain.model.user.*
import com.atiga.cakeorder.core.network.response.*
import com.atiga.cakeorder.core.utils.DataMapper

class RemoteDataSource(private val apiService: ApiService) {
    // category
    suspend fun getAllCategory(): List<CategoryResponseItem> {
        return apiService.getAllCategory()
    }

    suspend fun getCategoryById(id: Int): CategoryResponseItem {
        return apiService.getCategoryById(id)
    }

    suspend fun addCategory(data: AddCategory): CategoryResponseItem {
        return apiService.addCategory(data)
    }

    suspend fun editCategory(id: Int, data: Category): CategoryResponseItem {
        return apiService.editCategory(id, DataMapper.mapCategoryDomainToResponse(data))
    }

    suspend fun deleteCategory(id: Int): DeleteResponse {
        return apiService.deleteCategory(id)
    }

    // sub category
    suspend fun getAllSubCategory(): List<SubCategoryResponseItem> {
        return apiService.getAllSubCategory()
    }

    suspend fun getSubCategoriesByCategoryId(id: Int): List<SubCategoryResponseItem> {
        return apiService.getSubCategoriesByCategoryId(id)
    }

    suspend fun getSubCategoryById(id: Int): SubCategoryResponseItem {
        return apiService.getSubCategoryById(id)
    }

    suspend fun addSubCategory(id: Int, data: AddSubCategory): SubCategoryResponseItem {
        return apiService.addSubCategory(id, data)
    }

    suspend fun editSubCategory(id: Int, data: EditSubCategoryResponse): EditSubCategoryResponse {
        return apiService.editSubCategory(id, DataMapper.mapEditSubCategoryResponseToDomain(data))
    }

    suspend fun deleteSubCategory(id: Int): DeleteResponse {
        return apiService.deleteSubCategory(id)
    }

    suspend fun updateCapacity(id: Int, data: SubCategoryCapacity): SubCategoryResponseItem {
        return apiService.updateCapacity(id, data)
    }

    // product
    suspend fun getAllProduct(): List<ProductResponse> {
        return apiService.getAllProduct()
    }

    suspend fun getProductById(id: Int): ProductResponse {
        return apiService.getProductById(id)
    }

    suspend fun getProductBySubCategory(id: Int): List<ProductResponse> {
        return apiService.getProductBySubCategory(id)
    }

    suspend fun addProduct(data: AddProduct): ProductResponse {
        return apiService.addProduct(data)
    }

    suspend fun editProduct(id: Int, data: Product): ProductResponse {
        return apiService.editProduct(id, DataMapper.mapProductDomainToResponse(data))
    }

    suspend fun deleteProduct(id: Int): DeleteResponse {
        return apiService.deleteProduct(id)
    }

    suspend fun deleteProductImage(id: Int): DeleteResponse {
        return apiService.deleteProductImage(id)
    }

    // order
    suspend fun getUnfinishedOrder(): List<OrderResponse>{
        return apiService.getUnfinishedOrder()
    }

    suspend fun getOrderById(id: String): OrderResponse {
        return apiService.getOrderById(id)
    }

    suspend fun addOrder(userKey: String, data: AddOrder): OrderResponse {
        return apiService.addOrder(userKey, data)
    }

    suspend fun editOrder(orderNumber: String, itemKey: Int, data:EditOrderItem): ItemOrderResponse{
        return apiService.editOrder(orderNumber, itemKey, data)
    }

    suspend fun finishOrder(orderNumber: String, itemKey: Int, data: FinishingOrder): FinishOrderResponse {
        return apiService.finishOrder(orderNumber, itemKey, data)
    }

    suspend fun cancelOrder(orderNumber: String, itemKey: Int, data: CancelOrder, userKey: String): OrderResponse {
        return apiService.cancelOrder(orderNumber, itemKey, data, userKey)
    }

    suspend fun getCustomer(): List<CustomerResponse>{
        return apiService.getCustomer()
    }

    suspend fun getOrderByNameAndOrPhone(startDate: String, endDate: String, name: String, phone: String): List<OrderResponse> {
        return if(!name.isNullOrBlank()){
            if(!phone.isNullOrBlank()){
                apiService.getOrderByNameAndPhoneNumber(startDate, endDate, name, phone)
            } else {
                apiService.getOrderByName(startDate,endDate,name)
            }
        } else if(!phone.isNullOrBlank()){
            if(!name.isNullOrBlank()){
                apiService.getOrderByNameAndPhoneNumber(startDate, endDate, name, phone)
            } else {
                apiService.getOrderByPhoneNumber(startDate,endDate,phone)
            }
        } else {
            emptyList()
        }
    }

    // user
    suspend fun login(data: LoginUser): LoginResponse {
        return apiService.login(data)
    }

    suspend fun getUserInfo(token: String): User {
        return apiService.getUserInfo("Bearer $token")
    }

    suspend fun getAllUser(token: String): List<User> {
        return apiService.getAllUser("Bearer $token")
    }

    suspend fun addUser(data: CreateUser, token: String): LoginUser {
        return apiService.addUser(data, "Bearer $token")
    }

    suspend fun refreshToken(data: TokenUser): LoginResponse {
        return apiService.refreshToken(data)
    }

    suspend fun getUserRoles(): List<UserRole> {
        return apiService.getUserRoles()
    }

    // job dekor
    suspend fun getOrderToWork(fIsStarted: Boolean? = null,
                               fDate: String? = null,
                               fUser: String? = null,
                               fJobStartDate: String? = null,
                               fJobEndDate: String? = null): KitchenOrderResponse {
        return apiService.getOrderToWork(fIsStarted, fDate, fUser, fJobStartDate, fJobEndDate)
    }

    suspend fun startJobDekor(orderNumber: String, itemKey: Int): JobDekorResponse {
        return apiService.startJobDekor(orderNumber, itemKey)
    }

    suspend fun finishJobDekor(userKey: String, orderNumber: String, itemKey: Int, urutKey: Int): JobDekorResponse {
        return apiService.finishJobDekor(userKey, orderNumber, itemKey, urutKey)
    }

    // report
    suspend fun getReport(reportType: Int, data: Report): List<OrderResponse> {
        return apiService.getReport(reportType, data)
    }

    suspend fun trackOrder(orderNumber: String, itemKey: Int): List<TrackingResponse> {
        return apiService.trackOrder(orderNumber, itemKey)
    }
}