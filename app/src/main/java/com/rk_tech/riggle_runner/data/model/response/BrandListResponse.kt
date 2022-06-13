package com.rk_tech.riggle_runner.data.model.response

data class BrandListResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<BrandResults>
)

data class BrandResults(
    val code: String,
    val company: Int,
    val created_at: String,
    val doc_id: String,
    val id: Int,
    val image: String,
    val is_active: Boolean,
    val name: String,
    val update_url: String,
    val updated_at: String
)