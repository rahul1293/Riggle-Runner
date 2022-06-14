package com.rk_tech.riggle_runner.ui.main.search_store

import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.di.module.RepositoryModule
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchStoreVM @Inject constructor(

    private val networkHelper: ApiHelper
) : BaseViewModel() {
}