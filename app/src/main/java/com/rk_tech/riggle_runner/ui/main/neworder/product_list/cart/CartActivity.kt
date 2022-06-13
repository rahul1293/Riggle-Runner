package com.rk_tech.riggle_runner.ui.main.neworder.product_list.cart

import androidx.activity.viewModels
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.databinding.ActivityCartBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartActivity : BaseActivity<ActivityCartBinding>() {

    val viewModel: CartActivityVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.activity_cart
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {

    }
}