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
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
    /**
     * New implementation
     */
    override suspend fun sendOtp(request: SendOtpRequest): Response<SendOtpResponse> {
        return apiService.sendOtp(request)
    }

    override suspend fun verifyOtp(request: VerifyOtpRequest): Response<UserLoginResponse> {
        return apiService.verifyOtp(request)
    }

    override suspend fun getAuthPing(header: String): Response<UserLoginResponse> {
        return apiService.getAuthPing(header)
    }

    override suspend fun getDashboardData(
        header: String,
        id: Int,
        date: String
    ): Response<GetDashBoardResponse> {
        return apiService.getDashboardData(header, id, date)
    }

    override suspend fun getPendingCompleted(
        header: String,
        query: Map<String, String>
    ): Response<PendingCompleteResponse> {
        return apiService.getPendingCompleted(header, query)
    }

    override suspend fun getOrderDetailsApi(
        header: String,
        id: Int,
        query: Map<String, String>
    ): Response<OrderDetailResponse> {
        return apiService.getOrderDetailsApi(header, id, query)
    }

    override suspend fun getCoreConstant(header: String): Response<CoreConstants> {
        return apiService.getCoreConstant(header)
    }

    override suspend fun getBrandList(
        header: String,
        query: Map<String, String>
    ): Response<BrandResponse> {
        return apiService.getBrandList(header, query)
    }

    override suspend fun getBrandOffer(
        header: String,
        query: Map<String, String>
    ): Response<BrandOfferResponse> {
        return apiService.getBrandOffer(header, query)
    }

    override suspend fun editProductItem(
        header: String,
        orderId: Int?,
        data: EditProductRequest
    ): Response<ProductResponse> {
        return apiService.editProductItem(header, orderId, data)
    }

    override suspend fun getBrandProductList(
        header: String,
        query: Map<String, String>
    ): Response<BrandProductResponse> {
        return apiService.getBrandProductList(header, query)
    }

    override suspend fun getCartResponse(
        header: String,
        query: Map<String, String>
    ): Response<CartResponse> {
        return apiService.getCartResponse(header, query)
    }

    override suspend fun addCartProduct(
        header: String,
        orderId: Int?,
        request: EditProductRequest
    ): Response<AddCartResponse> {
        return apiService.addCartProduct(header, orderId, request)
    }

    override suspend fun getUserList(
        header: String,
        query: Map<String, String>
    ): Response<UserDeliverifyResponse> {
        return apiService.getUserList(header, query)
    }

    override suspend fun getRetailersList(
        header: String,
        id: Int,
        query: Map<String, String>
    ): Response<List<GetRetailsListItem>> {
        return apiService.getRetailersList(header, id, query)
    }

    override suspend fun createRetailer(
        header: String,
        data: Map<String, String>
    ): Response<CreateRetailerResponse> {
        return apiService.createRetailer(header, data)
    }

    override suspend fun setRevisitDate(
        header: String,
        id: Int,
        data: RevisitRequest
    ): Response<CancelOrderResponse> {
        return apiService.setRevisitDate(header, id, data)
    }

    override suspend fun getActivePinCodes(header: String, id: Int): Response<List<String>> {
        return apiService.getActivePinCodes(header, id)
    }

    override suspend fun placeOrder(
        header: String,
        request: PlaceOrderRequest
    ): Response<CancelOrderResponse> {
        return apiService.placeOrder(header, request)
    }

    override suspend fun collectPayment(
        header: String,
        id: Int,
        partMap: Map<String, RequestBody>,
        receipt: MultipartBody.Part?
    ): Response<CollectPaymentResponse> {
        return apiService.collectPayment(header, id, partMap, receipt)
    }

    /**
     * Old implementation
     */
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