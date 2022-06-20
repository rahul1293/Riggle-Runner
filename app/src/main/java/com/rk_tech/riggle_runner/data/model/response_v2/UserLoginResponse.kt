package com.rk_tech.riggle_runner.data.model.response_v2

data class UserLoginResponse(
    val max_age: Int,
    val session_id: String,
    val user: User
)

data class User(
    val company: Company,
    val created_at: String,
    val date_joined: String,
    val designation: Any,
    val email: String,
    val first_name: String,
    val full_name: String,
    val id: Int,
    val image: Any,
    val is_active: Boolean,
    val is_staff: Boolean,
    val is_superuser: Boolean,
    val last_login: String,
    val last_name: String,
    val manager: Any,
    val middle_name: String,
    val mobile: String,
    val permissions: List<String>,
    val role: String,
    val update_url: String,
    val updated_at: String,
    val user_id: Any,
    val username: Any
)

data class Company(
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

data class Extra(
    val company: Int,
    val created_at: String,
    val gst_number: Any,
    val id: Int,
    val is_gst_invoice: Boolean,
    val proof_file: Any,
    val proof_type: Any,
    val qr_code_text: Any,
    val updated_at: String
)