package com.rk_tech.riggle_runner.ui.main.settings

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.ServiceHubResponse
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsFragmentVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {

    var obrDetails = SingleRequestEvent<ServiceHubResponse>()
    fun getServiceHubDetails(authorization: String, userId: String) {
        viewModelScope.launch {
            obrDetails.postValue(Resource.loading(null))
            try {
                apiHelper.getServiceHubList(authorization, userId).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrDetails.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrDetails.postValue(
                            Resource.warn(
                                null,
                                it.errorBody()?.string().toString()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrDetails.postValue(Resource.error(null, it))
                }
            }
        }
    }

}