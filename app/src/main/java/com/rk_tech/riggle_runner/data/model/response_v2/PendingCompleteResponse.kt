package com.rk_tech.riggle_runner.data.model.response_v2

data class PendingCompleteResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<Result>
)

data class Result(
    val buyer: Buyer,
    val final_amount: Float,
    val id: Int,
    val status: String
)

data class Buyer(
    val admin: Admin,
    val full_address: String,
    val name: String
)

data class Admin(
    val mobile: String
)