package com.rk_tech.riggle_runner.data.model.response

data class RetailersResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<RetailerDetails>
)

data class RetailerDetails(
    val account_status: String,
    val address: String,
    val code: String,
    val created_at: String,
    val created_by: Any,
    val deleted_at: Any,
    val doc_id: String,
    val gstin: Any,
    val id: Int,
    val image: Any,
    val is_active: Boolean,
    val is_deleted: Boolean,
    val is_serviceable: Boolean,
    val landmark: String,
    val name: String,
    val pincode: String,
    val riggle_coins_balance: Int,
    val store_location: String,
    val store_type: String,
    val update_url: String,
    val updated_at: String
)