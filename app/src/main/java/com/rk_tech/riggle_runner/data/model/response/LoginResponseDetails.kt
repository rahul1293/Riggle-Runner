package com.rk_tech.riggle_runner.data.model.response

data class LoginResponseDetails(
    val max_age: Long,
    val session_id: String,
    val user: User
)

data class User(
    val created_at: String,
    val date_joined: String,
    val doc_id: String,
    val email: Any,
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
    val master_ops: Any,
    val middle_name: Any,
    val mobile: String,
    val permissions: List<String>,
    val retailer: Any,
    val role: String,
    val service_hub: ServiceHub,
    val update_url: String,
    val updated_at: String,
    val user_id: String,
    val username: Any
)

data class ServiceHub(
    val account_balance: Int,
    val code: String,
    val created_at: String,
    val deleted_at: Any,
    val doc_id: String,
    val fssai_certificate: Any,
    val fssai_number: Any,
    val gst_certificate: Any,
    val gst_number: Any,
    val id: Int,
    val is_active: Boolean,
    val is_deleted: Boolean,
    val name: String,
    val qr_code_image: String?,
    val riggle_coins_balance: Int,
    val update_url: String,
    val updated_at: String,
    val warehouse_address: String
)