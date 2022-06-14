package com.rk_tech.riggle_runner.ui.main.create_store

import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateStoreVM @Inject constructor(private val apiHelper: ApiHelper) :BaseViewModel(){
}