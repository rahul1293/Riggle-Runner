package com.rk_tech.riggle_runner.data.network.base

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.net.HttpURLConnection

open class SimpleApiResponse : Serializable {
    @SerializedName("status")
    var status = 0
        protected set

    @SerializedName("message")
    var message: String? = null
        protected set

    @SerializedName("method")
    var method: String? = null
        protected set

    override fun toString(): String {
        return "SimpleApiResponse{" +
                "success=" + status +
                ", message='" + message + '\'' +
                '}'
    }

    val isStatusOK: Boolean
        get() = status == HttpURLConnection.HTTP_OK
}