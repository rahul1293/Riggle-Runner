package com.rk_tech.riggle_runner.ui.login.otp

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request_v2.SendOtpRequest
import com.rk_tech.riggle_runner.data.model.request_v2.VerifyOtpRequest
import com.rk_tech.riggle_runner.data.model.response_v2.SendOtpResponse
import com.rk_tech.riggle_runner.data.model.response_v2.UserLoginResponse
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class EnterOtpActivityVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {

    var obrSendOtp = SingleRequestEvent<SendOtpResponse>()
    var obrOtpVerify = SingleRequestEvent<UserLoginResponse>()

    fun sendOtp(request: SendOtpRequest) {
        viewModelScope.launch {
            obrSendOtp.postValue(Resource.loading(null))
            try {
                apiHelper.sendOtp(request).let {
                    if (it.isSuccessful) {
                        obrSendOtp.postValue(
                            Resource.success(
                                it.body(),
                                it.body()?.message.toString()
                            )
                        )
                    } else {
                        obrSendOtp.postValue(Resource.warn(null, getErrorMessage(it.errorBody())))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrSendOtp.postValue(Resource.error(null, it))
                }
            }
        }
    }

    fun verifyOtp(request: VerifyOtpRequest) {
        viewModelScope.launch {
            obrOtpVerify.postValue(Resource.loading(null))
            try {
                apiHelper.verifyOtp(request).let {
                    if (it.isSuccessful) {
                        obrOtpVerify.postValue(
                            Resource.success(
                                it.body(),
                                "Success"
                            )
                        )
                    } else {
                        obrOtpVerify.postValue(Resource.warn(null, getErrorMessage(it.errorBody())))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrOtpVerify.postValue(Resource.error(null, it))
                }
            }
        }
    }

}