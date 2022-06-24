package com.rk_tech.riggle_runner.data.model.response_v2

data class BrandResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<BrandResult>
)

data class BrandResult(
    val code: Any,
    val company: Company,
    val created_at: String,
    val id: Int,
    val image: String?,
    val is_active: Boolean,
    val name: String,
    val pincodes: List<Any>,
    val update_url: String,
    val updated_at: String
)

/*data class Company(
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
)*/
