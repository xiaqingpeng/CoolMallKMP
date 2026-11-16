package com.joker.coolmallkmp.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AlipayAppPayRequest(
    val orderId: Long,
)
