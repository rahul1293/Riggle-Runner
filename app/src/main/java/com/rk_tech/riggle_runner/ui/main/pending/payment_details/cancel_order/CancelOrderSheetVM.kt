package com.rk_tech.riggle_runner.ui.main.pending.payment_details.cancel_order

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.CancelOrderResponse
import com.rk_tech.riggle_runner.data.model.response_v2.RunnerOrderCancellationReason
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CancelOrderSheetVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {
    var obrCancelOrder = SingleRequestEvent<CancelOrderResponse>()
    var obrReason = SingleRequestEvent<List<RunnerOrderCancellationReason>>()
    fun getCancelOrder(authorization: String, orderId: Int, reason: String) {
        val data = HashMap<String, String>()
        data["remark"] = reason
        viewModelScope.launch {
            obrCancelOrder.postValue(Resource.loading(null))
            try {
                apiHelper.cancelOrder(authorization, orderId, data).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrCancelOrder.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrCancelOrder.postValue(Resource.warn(null, it.errorBody()?.string().toString()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrCancelOrder.postValue(Resource.error(null, it))
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
                            obrReason.postValue(Resource.success(results.runner_order_cancellation_reasons, "Success"))
                        }
                    } else {
                        obrReason.postValue(Resource.warn(null, it.errorBody()?.string().toString()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrReason.postValue(Resource.error(null, it))
                }
            }
        }
    }
}