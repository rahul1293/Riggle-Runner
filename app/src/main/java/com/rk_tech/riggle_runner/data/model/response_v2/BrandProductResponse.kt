package com.rk_tech.riggle_runner.data.model.response_v2

data class BrandProductResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<ProductResult>
)

data class ProductResult(
    val base_quantity: Int,
    val base_rate: Float,
    val base_unit: String,
    val brand: Int,
    val code: String,
    val coins: Int,
    val combo_products: List<ComboProduct>?,
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
    var quantity: Int = 0,
    val updated_at: String
)

data class ComboProduct(
    val code: Any,
    val created_at: String,
    val id: Int,
    val is_active: Boolean,
    val name: String,
    val products: List<ProductResult>,
    val step: Int,
    val update_url: String,
    val updated_at: String
)