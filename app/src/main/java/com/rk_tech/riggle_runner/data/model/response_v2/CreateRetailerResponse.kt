package com.rk_tech.riggle_runner.data.model.response_v2

data class CreateRetailerResponse(
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

data class GetRetailerResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<CreateRetailerResponse>
)

class GetRetailsList : ArrayList<GetRetailsListItem>()

data class GetRetailsListItem(
    val account_status: String,
    val address_line: Any,
    val city: String,
    val code: String,
    val created_at: String,
    val extra: Extra,
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