package com.rk_tech.riggle_runner.ui.main.pending.orderdetails.collect_payment

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.DeliveryOrderResponse
import com.rk_tech.riggle_runner.data.model.response.OrderDetailsResponse
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.HashMap
import javax.inject.Inject

@HiltViewModel
class CollectPaymentActivityVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {
    var obrOrderDelivery = SingleRequestEvent<DeliveryOrderResponse>()
    fun donePayment(
        authorization: String,
        orderId: Int,
        dataRequest: HashMap<String, RequestBody>,
        body: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            obrOrderDelivery.postValue(Resource.loading(null))
            try {
                apiHelper.tripPayment(authorization, orderId, dataRequest, body).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrOrderDelivery.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrOrderDelivery.postValue(
                            Resource.warn(
                                null,
                                it.errorBody()?.string().toString()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrOrderDelivery.postValue(Resource.error(null, it))
                }
            }
        }
    }

    var obrOrderDetails = SingleRequestEvent<OrderDetailsResponse>()
    fun getOrderDetails(authorization: String, orderId: Int) {
        val query = HashMap<String, String>()
        query["expand"] = "service_hub,products.product.banner_image,products.free_product,products.product_combo"
        query["edit_view"] = "true"
        viewModelScope.launch {
            obrOrderDetails.postValue(Resource.loading(null))
            try {
                apiHelper.getOrderDetails(authorization, orderId, query).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrOrderDetails.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrOrderDetails.postValue(
                            Resource.warn(
                                null,
                                it.errorBody()?.string().toString()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrOrderDetails.postValue(Resource.error(null, it))
                }
            }
        }
    }

}