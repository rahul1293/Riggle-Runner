package com.rk_tech.riggle_runner.ui.welcome

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request_v2.VerifyOtpRequest
import com.rk_tech.riggle_runner.data.model.response_v2.UserLoginResponse
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeActivityVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {

    var obrAuthVerify = SingleRequestEvent<UserLoginResponse>()
    fun authPing(authorization: String) {
        viewModelScope.launch {
            obrAuthVerify.postValue(Resource.loading(null))
            try {
                apiHelper.getAuthPing(authorization).let {
                    if (it.isSuccessful) {
                        obrAuthVerify.postValue(
                            Resource.success(
                                it.body(),
                                "Success"
                            )
                        )
                    } else {
                        obrAuthVerify.postValue(Resource.warn(null, it.message()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrAuthVerify.postValue(Resource.error(null, it))
                }
            }
        }
    }

}