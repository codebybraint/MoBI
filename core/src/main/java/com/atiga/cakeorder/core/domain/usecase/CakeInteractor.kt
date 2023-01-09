package com.atiga.cakeorder.core.domain.usecase

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
import com.atiga.cakeorder.core.domain.repository.ICakeRepository
import com.atiga.cakeorder.core.network.response.EditSubCategoryResponse
import com.atiga.cakeorder.core.network.response.KitchenOrderResponse
import com.atiga.cakeorder.core.network.response.LoginResponse

class CakeInteractor(private val cakeRepository: ICakeRepository): CakeUseCase{

    override suspend fun getAllCategory(): List<Category> = cakeRepository.getAllCategory()
    override suspend fun getCategoryById(id: Int): Category = cakeRepository.getCategoryById(id)
    override suspend fun addCategory(data: AddCategory): Category = cakeRepository.addCategory(data)
    override suspend fun editCategory(id: Int, data: Category): Category = cakeRepository.editCategory(id, data)
    override suspend fun deleteCategory(id: Int): DeleteItem = cakeRepository.deleteCategory(id)

    override suspend fun getAllSubCategory(): List<SubCategory> = cakeRepository.getAllSubCategory()
    override suspend fun getSubCategoriesByCategoryId(id: Int): List<SubCategory> = cakeRepository.getSubCategoriesByCategoryId(id)
    override suspend fun getSubCategoryById(id: Int): SubCategory = cakeRepository.getSubCategoryById(id)
    override suspend fun addSubCategory(id: Int, data: AddSubCategory): SubCategory = cakeRepository.addSubCategory(id, data)
    override suspend fun editSubCategory(id: Int, data: EditSubCategoryResponse): EditSubCategory = cakeRepository.editSubCategory(id, data)
    override suspend fun deleteSubCategory(id: Int): DeleteItem = cakeRepository.deleteSubCategory(id)
    override suspend fun updateCapacity(id: Int, data: SubCategoryCapacity): SubCategory = cakeRepository.updateCapacity(id, data)

    override suspend fun getAllProduct(): List<Product> = cakeRepository.getAllProduct()
    override suspend fun getProductById(id: Int): Product = cakeRepository.getProductById(id)
    override suspend fun getProductBySubCategory(id: Int): List<Product> = cakeRepository.getProductBySubCategory(id)
    override suspend fun addProduct(data: AddProduct): Product = cakeRepository.addProduct(data)
    override suspend fun editProduct(id: Int, data: Product): Product = cakeRepository.editProduct(id, data)
    override suspend fun deleteProduct(id: Int): DeleteItem = cakeRepository.deleteProduct(id)
    override suspend fun deleteProductImage(id: Int): DeleteItem = cakeRepository.deleteProductImage(id)

    override suspend fun getUnfinishedOrder(): List<Order>  = cakeRepository.getUnfinishedOrder()
    override suspend fun getOrderById(id: String): Order = cakeRepository.getOrderById(id)
    override suspend fun addOrder(userKey: String, data: AddOrder): Order = cakeRepository.addOrder(userKey, data)
    override suspend fun editOrder(
        orderNumber: String,
        itemKey: Int,
        data: EditOrderItem
    ): ItemOrder =  cakeRepository.editOrder(orderNumber, itemKey, data)
    override suspend fun finishOrder(
        orderNumber: String,
        itemKey: Int,
        data: FinishingOrder
    ): FinishOrder = cakeRepository.finishOrder(orderNumber, itemKey, data)

    override suspend fun getOrderByNameAndOrPhone(startDate: String, endDate: String, name: String, phone: String): List<Order> = cakeRepository.getOrderByNameAndOrPhone(startDate, endDate, name, phone)

    override suspend fun cancelOrder(orderNumber: String, itemKey: Int, data: CancelOrder, userKey: String): Order = cakeRepository.cancelOrder(orderNumber, itemKey, data, userKey)
    override suspend fun getCustomer(): List<Customer> = cakeRepository.getCustomer()

    override suspend fun login(data: LoginUser): LoginResponse = cakeRepository.login(data)
    override suspend fun getUserInfo(token: String): User = cakeRepository.getUserInfo(token)
    override suspend fun getAllUser(token: String): List<User> = cakeRepository.getAllUser(token)
    override suspend fun addUser(data: CreateUser, token: String): LoginUser = cakeRepository.addUser(data, token)
    override suspend fun refreshToken(data: TokenUser): LoginResponse = cakeRepository.refreshToken(data)
    override suspend fun getUserRoles(): List<UserRole> = cakeRepository.getUserRoles()

    override suspend fun getOrderToWork(fIsStarted: Boolean?,
                                        fDate: String?,
                                        fUser: String?,
                                        fJobStartDate: String?,
                                        fJobEndDate: String?): KitchenOrderResponse = cakeRepository.getOrderToWork(fIsStarted, fDate, fUser, fJobStartDate, fJobEndDate)
    override suspend fun startJobDekor(orderNumber: String, itemKey: Int): JobDekor = cakeRepository.startJobDekor(orderNumber, itemKey)
    override suspend fun finishJobDekor(userKey: String, orderNumber: String, itemKey: Int, urutKey: Int): JobDekor = cakeRepository.finishJobDekor(userKey, orderNumber, itemKey, urutKey)

    override suspend fun getReport(reportType: Int, data: Report): List<Order> = cakeRepository.getReport(reportType, data)
    override suspend fun trackOrder(orderNumber: String, itemKey: Int): List<Tracking> = cakeRepository.trackOrder(orderNumber, itemKey)
}