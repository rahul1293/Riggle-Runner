package com.rk_tech.riggle_runner.ui.main.pending

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.SettlementsResponse
import com.rk_tech.riggle_runner.data.model.response_v2.GetDashBoardResponse
import com.rk_tech.riggle_runner.data.model.response_v2.Result
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingOrdersFragmentVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {

    var obrTripList = SingleRequestEvent<List<Result>>()
    var obrPaymentHistory = SingleRequestEvent<SettlementsResponse>()
    var obrDashboard = SingleRequestEvent<GetDashBoardResponse>()
    fun getTripList(authorization: String, type: String) {
//        val query = HashMap<String, String>()
//        query["page"] = type
//        query["delivery_type"] = "all"
//        query["expand"] = "retailer.admin"
//        viewModelScope.launch {
//            obrTripList.postValue(Resource.loading(null))
//            try {
//                apiHelper.getTripList(authorization, query).let {
//                    if (it.isSuccessful) {
//                        it.body()?.results?.let { results ->
//                            obrTripList.postValue(Resource.success(results, "Success"))
//                        }
//                    } else {
//                        obrTripList.postValue(Resource.warn(null, it.message()))
//                    }
//                }
//            } catch (e: Exception) {
//                parseException(e.cause)?.let {
//                    obrTripList.postValue(Resource.error(null, it))
//                }
//            }
//        }
    }

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

    fun getDashBoard(authorization: String, id: Int, date: String) {
        viewModelScope.launch {
            obrDashboard.postValue(Resource.loading(null))
            try {
                apiHelper.getDashboardData(authorization, id, date).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrDashboard.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrDashboard.postValue(Resource.warn(null, getErrorMessage(it.errorBody())))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrDashboard.postValue(Resource.error(null, it))
                }
            }
        }
    }

    fun getPendingList(authorization: String, query: HashMap<String, String>) {
        viewModelScope.launch {
            obrTripList.postValue(Resource.loading(null))
            try {
                apiHelper.getPendingCompleted(authorization, query).let {
                    if (it.isSuccessful) {
                        it.body()?.results?.let { results ->
                            obrTripList.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrTripList.postValue(Resource.warn(null, getErrorMessage(it.errorBody())))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrTripList.postValue(Resource.error(null, it))
                }
            }
        }
    }
}