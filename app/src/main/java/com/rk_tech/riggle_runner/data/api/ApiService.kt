package com.rk_tech.riggle_runner.data.api

import com.rk_tech.riggle_runner.data.model.User
import com.rk_tech.riggle_runner.data.model.request.LoginRequest
import com.rk_tech.riggle_runner.data.model.request.OrderRequest
import com.rk_tech.riggle_runner.data.model.request_v2.*
import com.rk_tech.riggle_runner.data.model.response.*
import com.rk_tech.riggle_runner.data.model.response_v2.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    /**
     * New api's
     */

    @Headers("x-app-name:runner")
    @POST("user/auth/send_otp/")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>

    @Headers("x-app-name:runner")
    @POST("user/auth/verify_otp/")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<UserLoginResponse>

    @Headers("x-app-name:runner")
    @GET("user/auth/ping/")
    suspend fun getAuthPing(
        @Header("Authorization") header: String
    ): Response<UserLoginResponse>

    @Headers("x-app-name:runner")
    @GET("core/companies/{id}/dashboard/")
    suspend fun getDashboardData(
        @Header("Authorization") header: String,
        @Path("id") id: Int,
        @Query("date") date: String
    ): Response<GetDashBoardResponse>

    @Headers("x-app-name:runner")
    @GET("core/orders/?")
    suspend fun getPendingCompleted(
        @Header("Authorization") header: String,
        @QueryMap query: Map<String, String>
    ): Response<PendingCompleteResponse>

    @Headers("x-app-name:runner")
    @GET("core/orders/{id}/?")
    suspend fun getOrderDetailsApi(
        @Header("Authorization") header: String,
        @Path("id") id: Int,
        @QueryMap query: Map<String, String>
    ): Response<OrderDetailResponse>

    @Headers("x-app-name:runner")
    @GET("core/constants/?")
    suspend fun getCoreConstant(
        @Header("Authorization") header: String
    ): Response<CoreConstants>

    @Headers("x-app-name:runner")
    @FormUrlEncoded
    @PATCH("core/orders/{id}/cancel/")
    suspend fun cancelOrder(
        @Header("Authorization") header: String,
        @Path("id") page: Int,
        @FieldMap data: Map<String, String>
    ): Response<CancelOrderResponse>

    @Headers("x-app-name:runner")
    @GET("core/brands/?")
    suspend fun getBrandList(
        @Header("Authorization") header: String,
        @QueryMap query: Map<String, String>
    ): Response<BrandResponse>

    @Headers("x-app-name:runner")
    @GET("core/offers/?")
    suspend fun getBrandOffer(
        @Header("Authorization") header: String,
        @QueryMap query: Map<String, String>
    ): Response<BrandOfferResponse>

    @Headers("x-app-name:runner")
    @FormUrlEncoded
    @PATCH("core/orders/{id}/update_products/")
    suspend fun editProductItem(
        @Header("Authorization") header: String,
        @Path("id") orderId: Int?,
        @Body data: EditProductRequest
    ): Response<ProductResponse>

    @Headers("x-app-name:runner")
    @GET("core/products/?")
    suspend fun getBrandProductList(
        @Header("Authorization") header: String,
        @QueryMap query: Map<String, String>
    ): Response<BrandProductResponse>

    @Headers("x-app-name:runner")
    @GET("core/cart/?")
    suspend fun getCartResponse(
        @Header("Authorization") header: String,
        @QueryMap query: Map<String, String>
    ): Response<CartResponse>

    @Headers("x-app-name:runner")
    @PATCH("core/cart/{id}/update_products/")
    suspend fun addCartProduct(
        @Header("Authorization") header: String,
        @Path("id") orderId: Int?,
        @Body request: EditProductRequest
    ): Response<AddCartResponse>

    @Headers("x-app-name:runner")
    @GET("user/users/?")
    suspend fun getUserList(
        @Header("Authorization") header: String,
        @QueryMap query: Map<String, String>
    ): Response<UserDeliverifyResponse>

    @Headers("x-app-name:runner")
    @GET("core/companies/{id}/buyers/?")
    suspend fun getRetailersList(
        @Header("Authorization") header: String,
        @Path("id") id: Int,
        @QueryMap query: Map<String, String>
    ): Response<List<GetRetailsListItem>>

    @Headers("x-app-name:runner")
    @POST("core/companies/")
    suspend fun createRetailer(
        @Header("Authorization") header: String,
        @FieldMap data: Map<String, String>
    ): Response<CreateRetailerResponse>

    @Headers("x-app-name:runner")
    @PATCH("core/orders/{id}/reschedule_visit/")
    suspend fun setRevisitDate(
        @Header("Authorization") header: String,
        @Path("id") id: Int,
        @Body data: RevisitRequest
    ): Response<CancelOrderResponse>

    @Headers("x-app-name:runner")
    @GET("core/companies/{id}/active_pincodes/")
    suspend fun getActivePinCodes(
        @Header("Authorization") header: String,
        @Path("id") id: Int
    ): Response<List<String>>

    @Headers("x-app-name:runner")
    @POST("core/orders/")
    suspend fun placeOrder(
        @Header("Authorization") header: String,
        @Body request: PlaceOrderRequest
    ): Response<CancelOrderResponse>

    /**
     * Old api's
     */
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @Headers("x-app-name:delivery_boy")
    @POST("user/auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseDetails>

    @GET("core/orders/")
    suspend fun getTripList(
        @Header("Authorization") header: String,
        @QueryMap query: Map<String, String>
    ): Response<PendingOrdersResponse>

    @GET("user/retailers?")
    suspend fun searchRetailers(
        @Header("Authorization") header: String,
        @QueryMap query: Map<String, String>
    ): Response<RetailersResponse>

    @FormUrlEncoded
    @POST("user/retailers/")
    suspend fun createRetailers(
        @Header("Authorization") header: String,
        @FieldMap data: Map<String, String>
    ): Response<RetailerDetails>

    @GET("core/products/?")
    suspend fun getProductList(
        @Header("Authorization") header: String,
        @QueryMap query: Map<String, String>
    ): Response<ProductResponse>

    @GET("core/orders/{id}/")
    suspend fun getOrderDetails(
        @Header("Authorization") header: String,
        @Path("id") page: Int,
        @QueryMap query: Map<String, String>
    ): Response<OrderDetailsResponse>

    @FormUrlEncoded
    @POST("core/orders/{id}/reschedule_payment/")
    suspend fun reSchedulePayment(
        @Header("Authorization") header: String,
        @Path("id") id: Int,
        @FieldMap data: Map<String, String>
    ): Response<ReschedulePaymentResponse>

    @FormUrlEncoded
    @POST("core/orders/{id}/deliver/")
    suspend fun deliveryOrder(
        @Header("Authorization") header: String,
        @Path("id") id: Int,
        @FieldMap data: Map<String, String>
    ): Response<DeliveryOrderResponse>

    @Multipart
    @POST("core/orders/{id}/pay/")
    suspend fun tripPayment(
        @Header("Authorization") header: String,
        @Path("id") id: Int,
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part receipt: MultipartBody.Part?
    ): Response<DeliveryOrderResponse>

    @GET("user/service-hubs/{id}/settlement_summary/")
    suspend fun getPaymentHistory(
        @Header("Authorization") header: String,
        @Path("id") hubId: Int
    ): Response<SettlementsResponse>

    @GET("user/service-hubs/{id}/active_pincodes/")
    suspend fun getOtpList(
        @Header("Authorization") header: String,
        @Path("id") hubId: Int?
    ): Response<OtpResponse>

    @GET("user/users/{id}/?expand=service_hub")
    suspend fun getUserDetails(
        @Header("Authorization") header: String,
        @Path("id") userId: String?
    ): Response<LoginResponseDetails>

    @POST("core/orders/complete_order/")
    suspend fun placeOrderByRunner(
        @Header("Authorization") header: String,
        @Body request: OrderRequest
    ): Response<List<DeliveryOrderResponse>>

    @GET("user/service-hubs/{id}/?expand=admins")
    suspend fun getServiceHubList(
        @Header("Authorization") header: String,
        @Path("id") userId: String?
    ): Response<ServiceHubResponse>

    @Headers("x-app-name:delivery_boy")
    @POST("core/orders/{id}/products/combo_update/")
    suspend fun editComboProductItem(
        @Header("Authorization") header: String,
        @Path("id") orderId: Int?,
        @Body request: RequestComboUpdate
    ): Response<List<ComboUpdateResponse>>

}
//https://api.riggleapp.in/api/v1/core/orders/1/products/combo_update/