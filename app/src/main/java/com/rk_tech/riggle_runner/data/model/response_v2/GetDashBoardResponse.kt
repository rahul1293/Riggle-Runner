package com.rk_tech.riggle_runner.data.model.response_v2

data class GetDashBoardResponse(
    val completed_orders: CompletedOrders,
    val pending_orders: PendingOrders
)

data class CompletedOrders(
    val amount: Float,
    val count: Int
)

data class PendingOrders(
    val amount: Float,
    val count: Int
)