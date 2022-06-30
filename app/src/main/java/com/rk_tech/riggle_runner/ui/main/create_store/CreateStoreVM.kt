package com.rk_tech.riggle_runner.ui.main.create_store

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request_v2.PlaceOrderRequest
import com.rk_tech.riggle_runner.data.model.response.CancelOrderResponse
import com.rk_tech.riggle_runner.data.model.response_v2.CreateRetailerResponse
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.HashMap
import javax.inject.Inject

@HiltViewModel
class CreateStoreVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {

    var obrPinList = SingleRequestEvent<List<String>>()
    var obrRetailer = SingleRequestEvent<CreateRetailerResponse>()
    var obrPlaceOrder = SingleRequestEvent<CancelOrderResponse>()
    fun getActivePinCodes(authorization: String, id: Int) {
        viewModelScope.launch {
            obrPinList.postValue(Resource.loading(null))
            try {
                apiHelper.getActivePinCodes(authorization, id).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrPinList.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrPinList.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrPinList.postValue(Resource.error(null, it))
                }
            }
        }
    }

    fun createStore(authorization: String, data: HashMap<String, String>) {
        viewModelScope.launch {
            obrRetailer.postValue(Resource.loading(null))
            try {
                apiHelper.createRetailer(authorization, data).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrRetailer.postValue(Resource.success(results, "Successfully created"))
                        }
                    } else {
                        obrRetailer.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrRetailer.postValue(Resource.error(null, it))
                }
            }
        }
    }

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