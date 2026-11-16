package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 用户订单统计（对齐 CoolMallKotlin `order/info/userCount`）
 *
 * 后端字段名为中文状态标签。
 */
@Serializable
data class OrderCount(
    @SerialName("待付款")
    val pendingPayment: Int = 0,
    @SerialName("待发货")
    val pendingShipment: Int = 0,
    @SerialName("待收货")
    val pendingReceive: Int = 0,
    @SerialName("待评价")
    val pendingReview: Int = 0,
    @SerialName("退款中")
    val refunding: Int = 0,
    @SerialName("已退款")
    val refunded: Int = 0,
) {
    val afterSale: Int get() = refunding + refunded
}
