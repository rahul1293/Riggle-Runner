package com.rk_tech.riggle_runner.data.model.response

data class ProductResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<ProductList>
)

data class ProductList(
    val base_quantity: Int,
    val base_rate: Int,
    val base_unit: String,
    val brand: Int,
    val category: Int,
    val code: String,
    val company_rate: Double,
    val company_step: Int,
    val created_at: String,
    val delivery_tat_days: Int,
    val description: String,
    val expiry_in_days: Int,
    val id: Int,
    val product: ProductX?,
    val inactive_pincodes: List<Any>,
    val is_active: Boolean,
    val name: String,
    val normalized_weight: Double,
    val retailer_step: Int,
    val riggle_coins: Int,
    val update_url: String,
    val updated_at: String,
    var quantity: Int = 0,
    val banner_image: BannerImage,
    val combo_products: List<ComboProducts>,
    val product_combo:Int?,
    val schemes: List<Schemes>
)

data class BannerImage(
    val created_at: String,
    val doc_id: String,
    val id: Int,
    val image: String,
    val is_banner: Boolean,
    val product: Int,
    val update_url: String,
    val updated_at: String
)

data class ComboProducts(
    val code: String,
    val created_at: String,
    val id: Int,
    val is_active: Boolean,
    val name: String,
    val products: List<ProductList>,
    val step: Int,
    val update_url: String,
    val updated_at: String
)

data class Schemes(
    val created_at: String,
    val free_product: Any,
    val free_quantity: Int,
    val id: Int,
    val is_active: Boolean,
    val max_quantity: Int,
    val min_quantity: Int,
    val product: Int,
    val rate: Double,
    val update_url: String,
    val updated_at: String
)

/*data class Product(
    val base_quantity: Int,
    val base_rate: Double,
    val base_unit: String,
    val brand: Int,
    val category: Int,
    val code: String,
    val company_rate: Double,
    val company_step: Int,
    val created_at: String,
    val delivery_tat_days: Int,
    val description: String,
    val expiry_in_days: Int,
    val id: Int,
    val inactive_pincodes: List<Any>,
    val is_active: Boolean,
    val name: String,
    val normalized_weight: Double,
    val retailer_step: Int,
    val riggle_coins: Int,
    val update_url: String,
    val updated_at: String
)*/

//class ComboUpdateResponse : ArrayList<ComboUpdateResponseItem>()

data class ComboUpdateResponse(
    val amount: Double,
    val created_at: String,
    val free_product: Any,
    val free_product_quantity: Int,
    val id: Int,
    val order: Int,
    val ordered_quantity: Int,
    val original_rate: Double,
    val product: Int,
    val product_combo: Int,
    val quantity: Int,
    val rate: Double,
    val riggle_coins: Int,
    val update_url: String,
    val updated_at: String
)