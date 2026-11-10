package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmOrder(
    val defaultAddress: Address? = null,
    val userCoupon: List<Coupon>? = emptyList(),
)
