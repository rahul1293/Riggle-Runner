package com.rk_tech.riggle_runner.ui.dashboard

import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.connectivity.ConnectivityProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardActivityVM @Inject constructor(
    private val apiHelper: ApiHelper,
    private val connectivityProvider: ConnectivityProvider
) : BaseViewModel() {

}