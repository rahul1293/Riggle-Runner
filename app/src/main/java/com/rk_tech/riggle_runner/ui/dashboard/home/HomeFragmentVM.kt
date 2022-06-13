package com.rk_tech.riggle_runner.ui.dashboard.home

import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.MenuBean
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeFragmentVM @Inject constructor(
    private val mainRepository: ApiHelper
) : BaseViewModel() {

    val obrOnlineStatus = SingleLiveEvent<Resource<MenuBean>>()

    fun updateOnlineStatus(authorization: String) {

        /*val io = Coroutines.io {
            obrOnlineStatus.postValue(Resource.loading(null))
                mainRepository.updateOnline(authorization, onlineStatus).let {
                    if (it.body()?.isStatus == true) {
                        it.body()?.success?.let {
                            obrOnlineStatus.postValue(Resource.success(it.data, it.message))
                        }
                    } else {
                        if (it.body()?.isStatus == false) {
                            it.body()?.success?.let { it1 ->
                                obrOnlineStatus.postValue(Resource.error(it1.code, it1.data))
                            }
                        } else {
                            obrOnlineStatus.postValue(setThrowableCode(it))
                        }
                    }
                }
            } else {
                obrOnlineStatus.postValue(Resource.error("No internet connection", null))
            }
        }*/

        /*viewModelScope.launch {
            obrOnlineStatus.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                mainRepository.updateOnline(authorization, onlineStatus).let {
                    if (it.body()?.isStatus == true) {
                        it.body()?.success?.let {
                            obrOnlineStatus.postValue(Resource.success(it.data, it.message))
                        }
                    } else {
                        if (it.body()?.isStatus == false) {
                            it.body()?.success?.let { it1 ->
                                obrOnlineStatus.postValue(Resource.error(it1.code, it1.data))
                            }
                        } else {
                            obrOnlineStatus.postValue(setThrowableCode(it))
                        }
                    }
                }
            } else {
                obrOnlineStatus.postValue(Resource.error("No internet connection", null))
            }
        }*/
    }

}