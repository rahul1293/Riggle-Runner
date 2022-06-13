package com.rk_tech.riggle_runner.data.model.request

data class CreateOrderRequest(
    val payment_mode: String,
    val products: List<SelectedProduct>,
    val redeemed_riggle_coins: Int,
    val retailer: Int
)

data class SelectedProduct(
    val product: Int,
    val quantity: Int
)

data class OrderRequest(
    val retailer: Int,
    val redeemed_riggle_coins: Int,
    val products: List<VariantUpdate>,
    val payment_mode : String
)

data class VariantUpdate(val product: Int?, val quantity: Int, val product_combo: Int?)

