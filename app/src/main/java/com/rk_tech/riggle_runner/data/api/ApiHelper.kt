package com.rk_tech.riggle_runner.data.api

import com.rk_tech.riggle_runner.data.model.User
import com.rk_tech.riggle_runner.data.model.request.LoginRequest
import com.rk_tech.riggle_runner.data.model.request.OrderRequest
import com.rk_tech.riggle_runner.data.model.request_v2.SendOtpRequest
import com.rk_tech.riggle_runner.data.model.request_v2.VerifyOtpRequest
import com.rk_tech.riggle_runner.data.model.response.*
import com.rk_tech.riggle_runner.data.model.response_v2.GetDashBoardResponse
import com.rk_tech.riggle_runner.data.model.response_v2.PendingCompleteResponse
import com.rk_tech.riggle_runner.data.model.response_v2.SendOtpResponse
import com.rk_tech.riggle_runner.data.model.response_v2.UserLoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiHelper {

    /**
     * New Calls
     */

    suspend fun sendOtp(request: SendOtpRequest): Response<SendOtpResponse>

    suspend fun verifyOtp(request: VerifyOtpRequest): Response<UserLoginResponse>

    suspend fun getAuthPing(header: String): Response<UserLoginResponse>

    suspend fun getDashboardData(
        header: String,
        id: Int,
        date: String
    ): Response<GetDashBoardResponse>

    suspend fun getPendingCompleted(
        header: String,
        query: Map<String, String>
    ): Response<PendingCompleteResponse>

    /**
     * Old Calls
     */
    suspend fun getUsers(): Response<List<User>>

    suspend fun login(request: LoginRequest): Response<LoginResponseDetails>

    suspend fun getTripList(
        header: String,
        query: Map<String, String>
    ): Response<PendingOrdersResponse>

    suspend fun getBrandList(
        header: String,
        query: Map<String, String>
    ): Response<BrandListResponse>

    suspend fun searchRetailers(
        header: String,
        query: Map<String, String>
    ): Response<RetailersResponse>

    suspend fun createRetailers(
        header: String,
        data: Map<String, String>
    ): Response<RetailerDetails>

    suspend fun getProductList(
        header: String,
        query: Map<String, String>
    ): Response<ProductResponse>

    suspend fun getOrderDetails(
        header: String, page: Int, query: Map<String, String>
    ): Response<OrderDetailsResponse>

    suspend fun cancelOrder(
        header: String, page: Int, data: Map<String, String>
    ): Response<CancelOrderResponse>

    suspend fun reSchedulePayment(
        header: String, id: Int, data: Map<String, String>
    ): Response<ReschedulePaymentResponse>

    suspend fun deliveryOrder(
        header: String, id: Int, data: Map<String, String>
    ): Response<DeliveryOrderResponse>

    suspend fun tripPayment(
        header: String,
        id: Int,
        partMap: HashMap<String, RequestBody>,
        receipt: MultipartBody.Part?
    ): Response<DeliveryOrderResponse>

    suspend fun getPaymentHistory(
        header: String, hubId: Int
    ): Response<SettlementsResponse>

    suspend fun editProductItem(
        header: String, orderId: Int?, productId: Int?, data: Map<String, String>
    ): Response<ProductResponse>

    suspend fun getOtpList(
        header: String, hubId: Int?
    ): Response<OtpResponse>

    suspend fun getUserDetails(
        header: String, userId: String?
    ): Response<LoginResponseDetails>

    suspend fun placeOrderByRunner(
        header: String,
        request: OrderRequest
    ): Response<List<DeliveryOrderResponse>>

    suspend fun getServiceHubList(
        header: String, userId: String?
    ): Response<ServiceHubResponse>

    suspend fun editComboProductItem(
        header: String, orderId: Int?, request: RequestComboUpdate
    ): Response<List<ComboUpdateResponse>>

}