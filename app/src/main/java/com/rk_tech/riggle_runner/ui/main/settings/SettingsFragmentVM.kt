package com.rk_tech.riggle_runner.ui.main.settings

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response_v2.ResultDeliverify
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsFragmentVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {

    var obrDetails = SingleRequestEvent<List<ResultDeliverify>>()
    fun getServiceHubDetails(authorization: String/*, userId: String*/) {
        val queryMap = HashMap<String, String>()
        queryMap["role"] = "company_admin"
        viewModelScope.launch {
            obrDetails.postValue(Resource.loading(null))
            try {
                //apiHelper.getServiceHubList(authorization, userId)
                apiHelper.getUserList(authorization, queryMap).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrDetails.postValue(Resource.success(results.results, "Success"))
                        }
                    } else {
                        obrDetails.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
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