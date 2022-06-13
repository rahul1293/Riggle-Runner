package com.rk_tech.riggle_runner.ui.base.location

import android.location.Location

interface LocationResultListener {
    fun getLocation(location: Location)
}