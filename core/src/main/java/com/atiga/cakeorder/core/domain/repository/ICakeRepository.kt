package com.atiga.cakeorder.core.domain.repository

import com.atiga.cakeorder.core.domain.model.DeleteItem
import com.atiga.cakeorder.core.domain.model.category.AddCategory
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.model.dekor.JobDekor
import com.atiga.cakeorder.core.domain.model.order.*
import com.atiga.cakeorder.core.domain.model.product.AddProduct
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.core.domain.model.report.Report
import com.atiga.cakeorder.core.domain.model.subcategory.AddSubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.EditSubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategoryCapacity
import com.atiga.cakeorder.core.domain.model.track.Tracking
import com.atiga.cakeorder.core.domain.model.user.*
import com.atiga.cakeorder.core.network.response.EditSubCategoryResponse
import com.atiga.cakeorder.core.network.response.KitchenOrderResponse
import com.atiga.cakeorder.core.network.response.LoginResponse

interface ICakeRepository{
    suspend fun getAllCategory(): List<Category>
    suspend fun getCategoryById(id: Int): Category
    suspend fun addCategory(data: AddCategory): Category
    suspend fun editCategory(id: Int, data: Category): Category
    suspend fun deleteCategory(id: Int): DeleteItem

    suspend fun getAllSubCategory(): List<SubCategory>
    suspend fun getSubCategoriesByCategoryId(id: Int): List<SubCategory>
    suspend fun getSubCategoryById(id: Int): SubCategory
    suspend fun addSubCategory(id: Int, data: AddSubCategory): SubCategory
    suspend fun editSubCategory(id: Int, data: EditSubCategoryResponse): EditSubCategory
    suspend fun deleteSubCategory(id: Int): DeleteItem
    suspend fun updateCapacity(id: Int, data: SubCategoryCapacity): SubCategory

    suspend fun getAllProduct(): List<Product>
    suspend fun getProductById(id: Int): Product
    suspend fun getProductBySubCategory(id: Int): List<Product>
    suspend fun addProduct(data: AddProduct): Product
    suspend fun editProduct(id: Int, data: Product): Product
    suspend fun deleteProduct(id: Int): DeleteItem
    suspend fun deleteProductImage(id: Int): DeleteItem

    suspend fun getUnfinishedOrder(): List<Order>
    suspend fun getOrderById(id: String): Order
    suspend fun addOrder(userKey: String, data: AddOrder): Order
    suspend fun editOrder(orderNumber: String, itemKey: Int, data: EditOrderItem): ItemOrder
    suspend fun finishOrder(orderNumber: String, itemKey: Int, data: FinishingOrder): FinishOrder
    suspend fun cancelOrder(orderNumber: String, itemKey: Int, data: CancelOrder, userKey: String): Order
    suspend fun getCustomer(): List<Customer>
    suspend fun getOrderByNameAndOrPhone(startDate: String, endDate: String, name: String, phone: String): List<Order>

    suspend fun login(data: LoginUser): LoginResponse
    suspend fun getUserInfo(token: String): User
    suspend fun getAllUser(token: String): List<User>
    suspend fun addUser(data: CreateUser, token: String): LoginUser
    suspend fun refreshToken(data: TokenUser): LoginResponse
    suspend fun getUserRoles(): List<UserRole>

    suspend fun getOrderToWork(fIsStarted: Boolean? = null,
                               fDate: String? = null,
                               fUser: String? = null,
                               fJobStartDate: String? = null,
                               fJobEndDate: String? = null): KitchenOrderResponse
    suspend fun startJobDekor(orderNumber: String, itemKey: Int): JobDekor
    suspend fun finishJobDekor(userKey: String, orderNumber: String, itemKey: Int, urutKey: Int): JobDekor

    suspend fun getReport(reportType: Int, data: Report): List<Order>
    suspend fun trackOrder(orderNumber: String, itemKey: Int): List<Tracking>
}