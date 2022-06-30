package com.rk_tech.riggle_runner.ui.main.search_store

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request_v2.PlaceOrderRequest
import com.rk_tech.riggle_runner.data.model.response.CancelOrderResponse
import com.rk_tech.riggle_runner.data.model.response_v2.CreateRetailerResponse
import com.rk_tech.riggle_runner.data.model.response_v2.GetRetailsListItem
import com.rk_tech.riggle_runner.data.model.response_v2.ResultDeliverify
import com.rk_tech.riggle_runner.di.module.RepositoryModule
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchStoreVM @Inject constructor(
    private val apiHelper: ApiHelper
) : BaseViewModel() {

    var obrRetailerList = SingleRequestEvent<List<GetRetailsListItem>>()
    fun getNearByRetailer(authorization: String, id: Int, search: String) {
        val queryMap = HashMap<String, String>()
        queryMap["search"] = search
        queryMap["suggested"] = "true"
        //queryMap["type"] = "retailer"
        viewModelScope.launch {
            obrRetailerList.postValue(Resource.loading(null))
            try {
                //apiHelper.getServiceHubList(authorization, userId)
                apiHelper.getRetailersList(authorization, id, queryMap).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrRetailerList.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrRetailerList.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrRetailerList.postValue(Resource.error(null, it))
                }
            }
        }
    }

    var obrPlaceOrder = SingleRequestEvent<CancelOrderResponse>()
    fun placeOrder(authorization: String, placeOrderRequest: PlaceOrderRequest) {
        viewModelScope.launch {
            obrPlaceOrder.postValue(Resource.loading(null))
            try {
                apiHelper.placeOrder(authorization, placeOrderRequest).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrPlaceOrder.postValue(Resource.success(results, "Successfully Placed"))
                        }
                    } else {
                        obrPlaceOrder.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrPlaceOrder.postValue(Resource.error(null, it))
                }
            }
        }
    }

}