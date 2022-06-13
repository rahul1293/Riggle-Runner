package com.rk_tech.riggle_runner.data.api

import com.rk_tech.riggle_runner.data.model.User
import com.rk_tech.riggle_runner.data.model.request.LoginRequest
import com.rk_tech.riggle_runner.data.model.request.OrderRequest
import com.rk_tech.riggle_runner.data.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun getUsers(): Response<List<User>> = apiService.getUsers()

    override suspend fun login(request: LoginRequest): Response<LoginResponseDetails> {
        return apiService.login(request)
    }

    override suspend fun getTripList(
        header: String,
        query: Map<String, String>
    ): Response<PendingOrdersResponse> {
        return apiService.getTripList(header, query)
    }

    override suspend fun getBrandList(
        header: String,
        query: Map<String, String>
    ): Response<BrandListResponse> {
        return apiService.getBrandList(header, query)
    }

    override suspend fun searchRetailers(
        header: String,
        query: Map<String, String>
    ): Response<RetailersResponse> {
        return apiService.searchRetailers(header, query)
    }

    override suspend fun createRetailers(
        header: String,
        data: Map<String, String>
    ): Response<RetailerDetails> {
        return apiService.createRetailers(header, data)
    }

    override suspend fun getProductList(
        header: String,
        query: Map<String, String>
    ): Response<ProductResponse> {
        return apiService.getProductList(header, query)
    }

    override suspend fun getOrderDetails(
        header: String,
        page: Int,
        query: Map<String, String>
    ): Response<OrderDetailsResponse> {
        return apiService.getOrderDetails(header, page, query)
    }

    override suspend fun cancelOrder(
        header: String,
        page: Int,
        data: Map<String, String>
    ): Response<CancelOrderResponse> {
        return apiService.cancelOrder(header, page, data)
    }

    override suspend fun reSchedulePayment(
        header: String,
        id: Int,
        data: Map<String, String>
    ): Response<ReschedulePaymentResponse> {
        return apiService.reSchedulePayment(header, id, data)
    }

    override suspend fun deliveryOrder(
        header: String,
        id: Int,
        data: Map<String, String>
    ): Response<DeliveryOrderResponse> {
        return apiService.deliveryOrder(header, id, data)
    }

    override suspend fun tripPayment(
        header: String,
        id: Int,
        partMap: HashMap<String, RequestBody>,
        receipt: MultipartBody.Part?
    ): Response<DeliveryOrderResponse> {
        return apiService.tripPayment(header, id, partMap, receipt)
    }

    override suspend fun getPaymentHistory(
        header: String,
        hubId: Int
    ): Response<SettlementsResponse> {
        return apiService.getPaymentHistory(header, hubId)
    }

    override suspend fun editProductItem(
        header: String,
        orderId: Int?,
        productId: Int?,
        data: Map<String, String>
    ): Response<ProductResponse> {
        return apiService.editProductItem(header, orderId, productId, data)
    }

    override suspend fun getOtpList(header: String, hubId: Int?): Response<OtpResponse> {
        return apiService.getOtpList(header, hubId)
    }

    override suspend fun getUserDetails(
        header: String,
        userId: String?
    ): Response<LoginResponseDetails> {
        return apiService.getUserDetails(header, userId)
    }

    override suspend fun placeOrderByRunner(
        header: String,
        request: OrderRequest
    ): Response<List<DeliveryOrderResponse>> {
        return apiService.placeOrderByRunner(header, request)
    }

    override suspend fun getServiceHubList(
        header: String,
        userId: String?
    ): Response<ServiceHubResponse> {
        return apiService.getServiceHubList(header, userId)
    }

    override suspend fun editComboProductItem(
        header: String,
        orderId: Int?,
        request: RequestComboUpdate
    ): Response<List<ComboUpdateResponse>> {
        return apiService.editComboProductItem(header, orderId, request)
    }

}