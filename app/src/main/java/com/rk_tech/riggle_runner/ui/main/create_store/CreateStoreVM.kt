package com.rk_tech.riggle_runner.ui.main.create_store

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response_v2.GetRetailsListItem
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

    }

}