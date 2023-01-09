package com.atiga.cakeorder.core.domain.repository

import android.util.Log
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
import com.atiga.cakeorder.core.network.RemoteDataSource
import com.atiga.cakeorder.core.network.response.EditSubCategoryResponse
import com.atiga.cakeorder.core.network.response.KitchenOrderResponse
import com.atiga.cakeorder.core.network.response.LoginResponse
import com.atiga.cakeorder.core.utils.DataMapper

class CakeRepository(private val remoteDataSource: RemoteDataSource): ICakeRepository {
    // category
    override suspend fun getAllCategory(): List<Category> {
        val result = remoteDataSource.getAllCategory()
        return DataMapper.mapListCategoryResponseToDomain(result)
    }

    override suspend fun getCategoryById(id: Int): Category {
        val result = remoteDataSource.getCategoryById(id)
        return DataMapper.mapCategoryResponseToDomain(result)
    }

    override suspend fun addCategory(data: AddCategory): Category {
        val result = remoteDataSource.addCategory(data)
        return DataMapper.mapCategoryResponseToDomain(result)
    }

    override suspend fun editCategory(id: Int, data: Category): Category {
        val result = remoteDataSource.editCategory(id, data)
        return DataMapper.mapCategoryResponseToDomain(result)
    }

    override suspend fun deleteCategory(id: Int): DeleteItem {
        val result = remoteDataSource.deleteCategory(id)
        return DataMapper.mapDeleteResponseToDomain(result)
    }

    override suspend fun getAllSubCategory(): List<SubCategory> {
        val result = remoteDataSource.getAllSubCategory()
        return DataMapper.mapListSubCategoryResponseToDomain(result)
    }

    override suspend fun getSubCategoriesByCategoryId(id: Int): List<SubCategory> {
        val result = remoteDataSource.getSubCategoriesByCategoryId(id)
        return DataMapper.mapListSubCategoryResponseToDomain(result)
    }

    override suspend fun getSubCategoryById(id: Int): SubCategory {
        val result = remoteDataSource.getSubCategoryById(id)
        return DataMapper.mapSubCategoryResponseToDomain(result)
    }

    override suspend fun addSubCategory(id: Int, data: AddSubCategory): SubCategory {
        val result = remoteDataSource.addSubCategory(id, data)
        return DataMapper.mapSubCategoryResponseToDomain(result)
    }

    override suspend fun editSubCategory(id: Int, data: EditSubCategoryResponse): EditSubCategory {
        val result = remoteDataSource.editSubCategory(id, data)
        return DataMapper.mapEditSubCategoryResponseToDomain(result)
    }

    override suspend fun deleteSubCategory(id: Int): DeleteItem {
        val result = remoteDataSource.deleteSubCategory(id)
        return DataMapper.mapDeleteResponseToDomain(result)
    }

    override suspend fun updateCapacity(id: Int, data: SubCategoryCapacity): SubCategory {
        val result = remoteDataSource.updateCapacity(id, data)
        return DataMapper.mapSubCategoryResponseToDomain(result)
    }

    // product
    override suspend fun getAllProduct(): List<Product> {
        val result = remoteDataSource.getAllProduct()
        return DataMapper.mapListProductResponseToDomain(result)
    }

    override suspend fun getProductById(id: Int): Product {
        val result = remoteDataSource.getProductById(id)
        return DataMapper.mapProductResponseToDomain(result)
    }

    override suspend fun getProductBySubCategory(id: Int): List<Product> {
        val result = remoteDataSource.getProductBySubCategory(id)
        return DataMapper.mapListProductResponseToDomain(result)
    }

    override suspend fun addProduct(data: AddProduct): Product {
        val result = remoteDataSource.addProduct(data)
        return DataMapper.mapProductResponseToDomain(result)
    }

    override suspend fun editProduct(id: Int, data: Product): Product {
        val result = remoteDataSource.editProduct(id, data)
        return DataMapper.mapProductResponseToDomain(result)
    }

    override suspend fun deleteProduct(id: Int): DeleteItem {
        val result = remoteDataSource.deleteProduct(id)
        return DataMapper.mapDeleteResponseToDomain(result)
    }

    override suspend fun deleteProductImage(id: Int): DeleteItem {
        val result = remoteDataSource.deleteProductImage(id)
        return DataMapper.mapDeleteResponseToDomain(result)
    }

    // order
    override suspend fun getUnfinishedOrder(): List<Order> {
        val result = remoteDataSource.getUnfinishedOrder()
        val mapped = DataMapper.mapListOrderResponseToDomain(result)
        Log.d("MappedData",mapped.toString())
        return mapped
    }

    override suspend fun getOrderById(id: String): Order {
        val result = remoteDataSource.getOrderById(id)
        val mapped = DataMapper.mapOrderResponseToDomain(result)
        Log.d("MappedData",mapped.toString())
        return mapped
    }

    override suspend fun addOrder(userKey: String, data: AddOrder): Order {
        val result = remoteDataSource.addOrder(userKey, data)
        return DataMapper.mapOrderResponseToDomain(result)
    }

    override suspend fun editOrder(
        orderNumber: String,
        itemKey: Int,
        data: EditOrderItem
    ): ItemOrder {
        val result = remoteDataSource.editOrder(orderNumber, itemKey, data)
        return DataMapper.mapItemOrderResponseToDomain(result)
    }

    override suspend fun finishOrder(orderNumber: String, itemKey: Int, data: FinishingOrder): FinishOrder {
        val result = remoteDataSource.finishOrder(orderNumber, itemKey, data)
        return DataMapper.mapFinishOrderResponseToDomain(result)
    }

    override suspend fun cancelOrder(
        orderNumber: String,
        itemKey: Int,
        data: CancelOrder,
        userKey: String
    ): Order {
        val result = remoteDataSource.cancelOrder(orderNumber, itemKey, data, userKey)
        return DataMapper.mapOrderResponseToDomain(result)
    }

    override suspend fun getCustomer(): List<Customer> {
        val result = remoteDataSource.getCustomer()
        return DataMapper.mapListCustomerResponseToDomain(result)
    }

    override suspend fun getOrderByNameAndOrPhone(
        startDate: String,
        endDate: String,
        name: String,
        phone: String
    ): List<Order> {
            val result = remoteDataSource.getOrderByNameAndOrPhone(startDate, endDate, name, phone)
            return DataMapper.mapListOrderResponseToDomain(result)
    }

    // user
    override suspend fun login(data: LoginUser): LoginResponse {
        return remoteDataSource.login(data)
    }

    override suspend fun getUserInfo(token: String): User {
        return remoteDataSource.getUserInfo(token)
    }

    override suspend fun getAllUser(token: String): List<User> {
        return remoteDataSource.getAllUser(token)
    }

    override suspend fun addUser(data: CreateUser, token: String): LoginUser {
        return remoteDataSource.addUser(data, token)
    }

    override suspend fun refreshToken(data: TokenUser): LoginResponse {
        return remoteDataSource.refreshToken(data)
    }

    override suspend fun getUserRoles(): List<UserRole> {
        return remoteDataSource.getUserRoles()
    }

    // order
    override suspend fun getOrderToWork(fIsStarted: Boolean?,
                                        fDate: String?,
                                        fUser: String?,
                                        fJobStartDate: String?,
                                        fJobEndDate: String?): KitchenOrderResponse {
        val result = remoteDataSource.getOrderToWork(fIsStarted, fDate, fUser, fJobStartDate, fJobEndDate)
        return result
    }

    override suspend fun startJobDekor(orderNumber: String, itemKey: Int): JobDekor {
        val result = remoteDataSource.startJobDekor(orderNumber, itemKey)
        return DataMapper.mapJobDekorResponseToDomain(result)
    }

    override suspend fun finishJobDekor(userKey: String, orderNumber: String, itemKey: Int, urutKey: Int): JobDekor {
        val result = remoteDataSource.finishJobDekor(userKey, orderNumber, itemKey, urutKey)
        return DataMapper.mapJobDekorResponseToDomain(result)
    }

    override suspend fun getReport(reportType: Int, data: Report): List<Order> {
        val result = remoteDataSource.getReport(reportType, data)
        return DataMapper.mapListOrderResponseToDomain(result)
    }

    override suspend fun trackOrder(orderNumber: String, itemKey: Int): List<Tracking> {
        val result = remoteDataSource.trackOrder(orderNumber, itemKey)
        return DataMapper.mapListTrackingResponseToDomain(result)
    }
}