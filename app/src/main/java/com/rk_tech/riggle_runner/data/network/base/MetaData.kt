package com.rk_tech.riggle_runner.data.network.base

import com.google.gson.annotations.SerializedName

class MetaData {
    @SerializedName("total_pages")
    var totalPages: Int = 0

    @SerializedName("current_page")
    var currentPage: Int = 0
}