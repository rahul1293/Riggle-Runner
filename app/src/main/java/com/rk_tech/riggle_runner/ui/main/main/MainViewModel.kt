package com.rk_tech.riggle_runner.ui.main.main

import androidx.lifecycle.*
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.User
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.connectivity.ConnectivityProvider
import com.rk_tech.riggle_runner.data.model.helper.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiHelper: ApiHelper,
    private val connectivityProvider: ConnectivityProvider
) : BaseViewModel() {

    private val _users = MutableLiveData<Resource<List<User>>>()
    val users: LiveData<Resource<List<User>>>
        get() = _users

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
     // connectivityProvider.getNetworkState().hasInternet()

       /* viewModelScope.launch {
            _users.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                mainRepository.getUsers().let {
                    if (it.isSuccessful) {
                        _users.postValue(Resource.success(it.body()))
                    } else _users.postValue(Resource.error(it.errorBody().toString(), null))
                }
            } else _users.postValue(Resource.error("No internet connection", null))
        }*/
    }
}