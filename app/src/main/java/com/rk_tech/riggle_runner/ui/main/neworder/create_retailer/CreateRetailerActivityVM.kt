package com.rk_tech.riggle_runner.ui.main.neworder.create_retailer

import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.OtpResponse
import com.rk_tech.riggle_runner.data.model.response.RetailerDetails
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.Coroutines
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.HashMap
import javax.inject.Inject

@HiltViewModel
class CreateRetailerActivityVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {

    var obrRetailer = SingleRequestEvent<RetailerDetails>()
    var obrPinCodes = SingleRequestEvent<List<String>>()
    fun createNewRetailer(authorization: String, data: HashMap<String, String>) {
        Coroutines.io {
            obrRetailer.postValue(Resource.loading(null))
            try {
                apiHelper.createRetailers(authorization, data).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrRetailer.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrRetailer.postValue(Resource.warn(null, it.errorBody()?.string().toString()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrRetailer.postValue(Resource.error(null, it))
                }
            }
        }
    }

    fun getPinCodes(authorization: String, hubId: Int) {
        Coroutines.io {
            obrPinCodes.postValue(Resource.loading(null))
            try {
                apiHelper.getOtpList(authorization, hubId).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrPinCodes.postValue(Resource.success(results.pincodes, "Success"))
                        }
                    } else {
                        obrPinCodes.postValue(Resource.warn(null, it.message()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrPinCodes.postValue(Resource.error(null, it))
                }
            }
        }
    }

}