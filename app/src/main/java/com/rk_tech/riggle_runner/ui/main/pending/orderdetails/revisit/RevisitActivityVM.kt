package com.rk_tech.riggle_runner.ui.main.pending.orderdetails.revisit

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.ReschedulePaymentResponse
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RevisitActivityVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {

    var obrRevisit = SingleRequestEvent<ReschedulePaymentResponse>()
    fun reSchedulePayment(authorization: String, orderId: Int, reason: String, date: String) {
        val data = HashMap<String, String>()
        data["payment_reschedule_reason"] = reason
        data["payment_rescheduled_to"] = date
        viewModelScope.launch {
            obrRevisit.postValue(Resource.loading(null))
            try {
                apiHelper.reSchedulePayment(authorization, orderId, data).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrRevisit.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrRevisit.postValue(Resource.warn(null, it.errorBody()?.string().toString()))
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