package com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet

import com.rk_tech.riggle_runner.data.model.response.Schemes

interface ProductChooseListener {
    fun itemUpdated(scheme: Schemes, pos: Int)

    fun onRefresh() {

    }
}