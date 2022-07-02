package com.rk_tech.riggle_runner.ui.main.pending.orderdetails.payment_sheet

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request_v2.RevisitRequest
import com.rk_tech.riggle_runner.data.model.response.CancelOrderResponse
import com.rk_tech.riggle_runner.data.model.response_v2.CollectPaymentResponse
import com.rk_tech.riggle_runner.data.model.response_v2.PaymentRescheduleReason
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
class PaymentBottomSheetVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {
    var obrOrderDelivery = SingleRequestEvent<CollectPaymentResponse>()
    var obrReason = SingleRequestEvent<List<PaymentRescheduleReason>>()
    var obrRevisit = SingleRequestEvent<CancelOrderResponse>()
    fun collectPayment(
        authorization: String,
        orderId: Int,
        dataRequest: HashMap<String, RequestBody>,
        body: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            obrOrderDelivery.postValue(Resource.loading(null))
            try {
                apiHelper.collectPayment(authorization, orderId, dataRequest, body).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrOrderDelivery.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrOrderDelivery.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
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

    fun getCancellationReason(authorization: String) {
        viewModelScope.launch {
            obrReason.postValue(Resource.loading(null))
            try {
                apiHelper.getCoreConstant(authorization).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrReason.postValue(
                                Resource.success(
                                    results.runner_payment_reschedule_reasons,
                                    "Success"
                                )
                            )
                        }
                    } else {
                        obrReason.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrReason.postValue(Resource.error(null, it))
                }
            }
        }
    }

    fun setRevisitDate(authorization: String, id: Int, revisitRequest: RevisitRequest) {
        viewModelScope.launch {
            obrRevisit.postValue(Resource.loading(null))
            try {
                apiHelper.setRevisitDate(authorization, id, revisitRequest).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrRevisit.postValue(
                                Resource.success(
                                    results,
                                    "Success"
                                )
                            )
                        }
                    } else {
                        obrRevisit.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrRevisit.postValue(Resource.error(null, it))
                }
            }
        }
    }

}