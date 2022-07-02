package com.rk_tech.riggle_runner.data.model.response_v2

data class CoreConstants(
    val base_units: List<BaseUnit>,
    val category_types: List<CategoryType>,
    val company_proof_types: List<CompanyProofType>,
    val offer_types: List<OfferType>,
    val order_statuses: List<OrderStatuse>,
    val pack_units: List<PackUnit>,
    val payment_modes: List<PaymentMode>,
    val payment_reschedule_reasons: List<PaymentRescheduleReason>,
    val payment_statuses: List<PaymentStatuse>,
    val play_order_cancellation_reasons: List<PlayOrderCancellationReason>,
    val play_proof_types: List<PlayProofType>,
    val region_types: List<RegionType>,
    val retailer_order_cancellation_reasons: List<RetailerOrderCancellationReason>,
    val runner_order_cancellation_reasons: List<RunnerOrderCancellationReason>,
    val runner_payment_modes: List<RunnerPaymentMode>,
    val runner_payment_reschedule_reasons: List<PaymentRescheduleReason>
)

data class BaseUnit(
    val name: String,
    val value: String
)

data class CategoryType(
    val name: String,
    val value: String
)

data class CompanyProofType(
    val name: String,
    val value: String
)

data class OfferType(
    val name: String,
    val value: String
)

data class OrderStatuse(
    val name: String,
    val value: String
)

data class PackUnit(
    val name: String,
    val value: String
)

data class PaymentMode(
    val name: String,
    val value: String
)

data class PaymentRescheduleReason(
    val name: String,
    val value: String
)

data class PaymentStatuse(
    val name: String,
    val value: String
)

data class PlayOrderCancellationReason(
    val name: String,
    val value: String
)

data class PlayProofType(
    val name: String,
    val value: String
)

data class RegionType(
    val name: String,
    val value: String
)

data class RetailerOrderCancellationReason(
    val name: String,
    val value: String
)

data class RunnerOrderCancellationReason(
    val name: String,
    val value: String,
    var check: Boolean = false
)

data class RunnerPaymentMode(
    val name: String,
    val value: String
)