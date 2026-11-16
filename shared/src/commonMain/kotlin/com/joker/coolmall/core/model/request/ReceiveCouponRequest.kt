package com.joker.coolmallkmp.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ReceiveCouponRequest(
    val couponId: Long,
)
