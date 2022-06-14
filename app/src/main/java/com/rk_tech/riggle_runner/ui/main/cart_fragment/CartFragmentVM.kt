package com.rk_tech.riggle_runner.ui.main.cart_fragment

import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartFragmentVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {

}