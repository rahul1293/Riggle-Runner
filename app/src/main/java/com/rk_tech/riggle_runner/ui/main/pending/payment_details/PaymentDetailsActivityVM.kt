package com.rk_tech.riggle_runner.ui.main.pending.payment_details

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.SettlementsResponse
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentDetailsActivityVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {
    var obrPaymentHistory = SingleRequestEvent<SettlementsResponse>()
    fun settlementHistory(authorization: String, hub_id: Int) {
        viewModelScope.launch {
            obrPaymentHistory.postValue(Resource.loading(null))
            try {
                apiHelper.getPaymentHistory(authorization, hub_id).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrPaymentHistory.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrPaymentHistory.postValue(
                            Resource.warn(
                                null,
                                it.errorBody()?.string().toString()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrPaymentHistory.postValue(Resource.error(null, it))
                }
            }
        }
    }
}