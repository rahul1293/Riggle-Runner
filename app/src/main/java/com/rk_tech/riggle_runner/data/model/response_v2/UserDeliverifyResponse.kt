package com.rk_tech.riggle_runner.data.model.response_v2

data class UserDeliverifyResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<ResultDeliverify>
)

data class ResultDeliverify(
    val company: Int,
    val created_at: String,
    val date_joined: String,
    val designation: Any,
    val email: Any,
    val first_name: String,
    val full_name: String,
    val id: Int,
    val image: Any,
    val is_active: Boolean,
    val is_staff: Boolean,
    val is_superuser: Boolean,
    val last_login: Any,
    val last_name: String,
    val manager: Any,
    val middle_name: String,
    val mobile: String,
    val permissions: List<String>,
    val role: String,
    val update_url: String,
    val updated_at: String,
    val user_id: Any,
    val username: String
)