package com.joker.coolmall.core.model.request

import kotlinx.serialization.Serializable

/**
 * 取消订单请求（对齐 CoolMallKotlin CancelOrderRequest）
 */
@Serializable
data class CancelOrderRequest(
    val orderId: Long,
    val remark: String,
)
