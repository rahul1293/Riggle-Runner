package com.rk_tech.riggle_runner.ui.login

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request.LoginRequest
import com.rk_tech.riggle_runner.data.model.response.LoginResponseDetails
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.connectivity.ConnectivityProvider
import com.rk_tech.riggle_runner.utils.event.SingleLiveEvent
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

}