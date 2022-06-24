package com.rk_tech.riggle_runner.data.model.response_v2

data class BrandOfferResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<OfferResult>
)

data class OfferResult(
    val available_quantity: Int,
    val brand: Int,
    val code: String,
    val company: Int,
    val created_at: String,
    val description: String,
    val discount_amount: Int,
    val discount_type: String,
    val expiry: Any,
    val id: Int,
    val is_active: Boolean,
    val min_amount: Int,
    val ordered_quantity: Int,
    val payment_amount: Int,
    val title: String,
    val type: String,
    val update_url: String,
    val updated_at: String,
    val usage_type: String
)