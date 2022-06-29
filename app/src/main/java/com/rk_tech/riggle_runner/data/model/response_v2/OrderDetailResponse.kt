package com.rk_tech.riggle_runner.data.model.response_v2

data class OrderDetailResponse(
    val assigned_runner: Int,
    val buyer: BuyerOne,
    val challan_file: Any,
    val code: Any,
    val created_at: String,
    val delivery_date: String,
    val earned_coins: Int,
    val final_amount: Int,
    val id: Int,
    val is_cart: Boolean,
    val paid_amount: Int,
    val pending_amount: Int,
    val product_amount: Int,
    val products: List<Product>,
    val redeemed_coins: Int,
    val seller: Any,
    var status: String,
    val total_discount_amount: Int,
    val update_url: String,
    val updated_at: String
)

data class BuyerOne(
    val account_status: String,
    val address_line: Any,
    val city: String,
    val code: String,
    val created_at: String,
    val full_address: String,
    val id: Int,
    val is_active: Boolean,
    val lat: Any,
    val locality: Any,
    val logo: Any,
    val long: Any,
    val name: String,
    val pincode: String,
    val short_address: String,
    val state: String,
    val type: String,
    val update_url: String,
    val updated_at: String
)

data class Product(
    val amount: Int,
    val coins: Int,
    val created_at: String,
    val free_product: Any,
    val free_product_quantity: Int,
    val id: Int,
    val order: Int,
    val ordered_quantity: Int,
    val original_rate: Float,
    val product: ProductX?,
    val product_combo: Any,
    var quantity: Int,
    val rate: Float,
    val update_url: String,
    val updated_at: String,
    val products: Any
)

data class ProductX(
    val base_quantity: Int,
    val base_rate: Any,
    val base_unit: Any,
    val brand: Int,
    val code: Any,
    val coins: Int,
    val created_at: String,
    val delivery_tat_days: Int,
    val description: String,
    val id: Int,
    val is_active: Boolean,
    val name: String,
    val normalized_weight: Double,
    val retailer_moq: Int,
    val sub_category: Any,
    val update_url: String,
    val updated_at: String
)