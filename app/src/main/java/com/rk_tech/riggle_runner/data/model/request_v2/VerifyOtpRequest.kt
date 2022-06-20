package com.rk_tech.riggle_runner.data.model.request_v2

data class VerifyOtpRequest(
    val mobile: String,
    val otp: String
)