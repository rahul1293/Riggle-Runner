package com.rk_tech.riggle_runner.data.model.response_v2

data class CollectPaymentResponse(
    val assigned_runner: Int,
    val buyer: Int,
    val cart_user: Any,
    val challan_file: Any,
    val code: Any,
    val created_at: String,
    val delivery_date: String,
    val earned_coins: Int,
    val final_amount: Double,
    val id: Int,
    val invoice_file: Any,
    val is_cart: Boolean,
    val paid_amount: Double,
    val pending_amount: Double,
    val product_amount: Double,
    val redeemed_coins: Int,
    val seller: Int,
    val status: String,
    val total_discount_amount: Double,
    val update_url: String,
    val updated_at: String
)