package com.rk_tech.riggle_runner.ui.login

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request.LoginRequest
import com.rk_tech.riggle_runner.data.model.request_v2.SendOtpRequest
import com.rk_tech.riggle_runner.data.model.response.LoginResponseDetails
import com.rk_tech.riggle_runner.data.model.response_v2.SendOtpResponse
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginActivityVM @Inject constructor(
    private val apiHelper: ApiHelper
) : BaseViewModel() {

    var obrLogin = SingleRequestEvent<LoginResponseDetails>()
    var obrSendOtp = SingleRequestEvent<SendOtpResponse>()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            obrLogin.postValue(Resource.loading(null))
            try {
                apiHelper.login(request).let {
                    if (it.isSuccessful) {
                        obrLogin.postValue(Resource.success(it.body(), "Success"))
                    } else {
                        obrLogin.postValue(Resource.warn(null, it.message()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrLogin.postValue(Resource.error(null, it))
                }
            }
        }
    }

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

}