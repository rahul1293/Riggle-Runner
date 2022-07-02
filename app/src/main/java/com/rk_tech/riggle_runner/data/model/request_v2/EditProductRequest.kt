package com.rk_tech.riggle_runner.data.model.request_v2

data class EditProductRequest(
    val products: List<ProductEditData>
)

data class ProductEditData(
    val product: Int,
    val quantity: Int,
    val product_combo: Int?
)