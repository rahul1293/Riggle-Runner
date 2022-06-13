package com.rk_tech.riggle_runner.data.network.base

import com.google.gson.annotations.SerializedName

class PageResponse<T> : ApiResponse<T>() {
    @SerializedName("meta_data")
    val metaData: MetaData = MetaData()
}