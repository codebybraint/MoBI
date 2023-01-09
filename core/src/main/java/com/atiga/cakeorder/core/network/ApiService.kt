package com.atiga.cakeorder.core.network

import com.atiga.cakeorder.core.domain.model.category.AddCategory
import com.atiga.cakeorder.core.domain.model.order.AddOrder
import com.atiga.cakeorder.core.domain.model.order.CancelOrder
import com.atiga.cakeorder.core.domain.model.order.EditOrderItem
import com.atiga.cakeorder.core.domain.model.order.FinishingOrder
import com.atiga.cakeorder.core.domain.model.product.AddProduct
import com.atiga.cakeorder.core.domain.model.report.Report
import com.atiga.cakeorder.core.domain.model.subcategory.AddSubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.EditSubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategoryCapacity
import com.atiga.cakeorder.core.domain.model.user.*
import com.atiga.cakeorder.core.network.response.*
import retrofit2.http.*

interface ApiService {
    // category
    @GET("kategori")
    suspend fun getAllCategory(): List<CategoryResponseItem>

    @GET("kategori/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): CategoryResponseItem

    @POST("kategori")
    suspend fun addCategory(@Body data: AddCategory): CategoryResponseItem

    @PUT("kategori/{id}")
    suspend fun editCategory(@Path("id") id: Int, @Body data: CategoryResponseItem): CategoryResponseItem

    @DELETE("kategori/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): DeleteResponse

    // sub category
    @GET("subkategori")
    suspend fun getAllSubCategory(): List<SubCategoryResponseItem>

    @GET("subkategori/kategori/{id}")
    suspend fun getSubCategoriesByCategoryId(@Path("id") id: Int): List<SubCategoryResponseItem>

    @GET("subkategori/{id}")
    suspend fun getSubCategoryById(@Path("id") id: Int): SubCategoryResponseItem

    @POST("subkategori/kategori/{id}")
    suspend fun addSubCategory(@Path("id") id: Int, @Body data: AddSubCategory): SubCategoryResponseItem

    @PUT("subkategori/{id}")
    suspend fun editSubCategory(@Path("id") id: Int, @Body data: EditSubCategory): EditSubCategoryResponse

    @DELETE("subkategori/{id}")
    suspend fun deleteSubCategory(@Path("id") id: Int): DeleteResponse

    @PUT("subkategori/{id}/maxorder")
    suspend fun updateCapacity(@Path("id") id: Int, @Body data: SubCategoryCapacity): SubCategoryResponseItem

    // product
    @GET("products")
    suspend fun getAllProduct(): List<ProductResponse>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductResponse

    @GET("products/subkategori/{id}")
    suspend fun getProductBySubCategory(@Path("id") id: Int): List<ProductResponse>

    @POST("products")
    suspend fun addProduct(@Body data: AddProduct): ProductResponse

    @PUT("products/{id}")
    suspend fun editProduct(@Path("id") id: Int, @Body data: ProductResponse): ProductResponse

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): DeleteResponse

    @DELETE("products/images/{id}")
    suspend fun deleteProductImage(@Path("id") id: Int): DeleteResponse

    // order
    @GET("orders/unfinished")
    suspend fun getUnfinishedOrder(): List<OrderResponse>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): OrderResponse

    @POST("orders")
    suspend fun addOrder(@Header("userKey") userKey: String, @Body data: AddOrder): OrderResponse

    @PUT("orders/{orderNumber}/items/{itemKey}")
    suspend fun editOrder(@Path("orderNumber") orderNumber: String, @Path("itemKey") itemKey: Int, @Body data: EditOrderItem): ItemOrderResponse

    @POST("orders/{orderNumber}/items/{itemKey}/finished")
    suspend fun finishOrder(@Path("orderNumber") orderNumber: String, @Path("itemKey") itemKey: Int, @Body data: FinishingOrder): FinishOrderResponse

    @POST("orders/{orderNumber}/items/{itemKey}/cancel")
    suspend fun cancelOrder(@Path("orderNumber") orderNumber: String, @Path("itemKey") itemKey: Int, @Body data: CancelOrder, @Header("userKey") userKey: String): OrderResponse

    @GET("orders/customers")
    suspend fun getCustomer(): List<CustomerResponse>

    @GET("orders")
    suspend fun getOrderByName(@Query("s") startDate: String, @Query("e") endDate: String, @Query("n") name: String): List<OrderResponse>

    @GET("orders")
    suspend fun getOrderByPhoneNumber(@Query("s")startDate: String, @Query("e") endDate: String, @Query("p") phone: String ): List<OrderResponse>

    @GET("orders")
    suspend fun getOrderByNameAndPhoneNumber(@Query("s") startDate: String, @Query("e") endDate: String, @Query("n") name: String, @Query("p") phone: String): List<OrderResponse>

    // user
    @POST("users/login")
    suspend fun login(@Body data: LoginUser): LoginResponse

    @GET("users")
    suspend fun getUserInfo(@Header("Authorization") authHeader: String): User

    @GET("users/all")
    suspend fun getAllUser(@Header("Authorization") authHeader: String): List<User>

    @POST("users")
    suspend fun addUser(@Body data: CreateUser, @Header("Authorization") authHeader: String): LoginUser

    @POST("users/tokens/refresh")
    suspend fun refreshToken(@Body data: TokenUser): LoginResponse

    @GET("users/roles")
    suspend fun getUserRoles(): List<UserRole>

    // job dekor
    @GET("jobdekor/orders")
    suspend fun getOrderToWork(
        @Query("fIsStarted") fIsStarted: Boolean? = null,
        @Query("fDate") fDate: String? = null,
        @Query("fUser") fUser: String? = null,
        @Query("fJobStartDate") fJobStartDate: String? = null,
        @Query("fJobEndDate") fJobEndDate: String? = null
        ): KitchenOrderResponse

    @POST("jobdekor/orders/{orderNumber}/items/{itemKey}/start")
    suspend fun startJobDekor(@Path("orderNumber") orderNumber: String, @Path("itemKey") itemKey: Int): JobDekorResponse

    @POST("jobdekor/orders/{orderNumber}/items/{itemKey}/keys/{urutKey}/finish")
    suspend fun finishJobDekor(@Header("userKey") userKey: String, @Path("orderNumber") orderNumber: String, @Path("itemKey") itemKey: Int, @Path("urutKey") urutKey: Int): JobDekorResponse

    // report
    @POST("reports/{reportType}")
    suspend fun getReport(@Path("reportType") reportType: Int, @Body data: Report): List<OrderResponse>

    @GET("reports/tracking/orders/{orderNumber}/items/{itemKey}")
    suspend fun trackOrder(@Path("orderNumber") orderNumber: String, @Path("itemKey") itemKey: Int): List<TrackingResponse>
}