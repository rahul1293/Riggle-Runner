package com.rk_tech.riggle_runner.data.model.response_v2

data class SendOtpResponse(
    val `data`: Data,
    val message: String
)

data class Data(
    val already_exists: Boolean
)