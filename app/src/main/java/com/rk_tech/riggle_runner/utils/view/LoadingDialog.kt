package com.rk_tech.riggle_runner.utils.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.rk_tech.riggle_runner.R

class LoadingDialog(context: Context?) : Dialog(context!!) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
    }
}