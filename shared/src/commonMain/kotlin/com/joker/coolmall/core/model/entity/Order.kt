package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 订单信息（对齐 CoolMallKotlin Order）
 *
 * status: 0-待付款 1-待发货 2-待收货 3-待评价 4-交易完成 5-退款中 6-已退款 7-已关闭
 * payType: 0-待支付 1-微信 2-支付宝
 */
@Serializable
data class Order(
    val id: Long = 0,
    val userId: Long = 0,
    val title: String? = null,
    val payType: Int = 0,
    val payTime: String? = null,
    val orderNum: String = "",
    val status: Int = 0,
    val price: Int = 0,
    val discountPrice: Int = 0,
    val realPrice: Int = 0,
    val address: Address? = null,
    val refundStatus: Int? = null,
    val remark: String? = null,
    val closeRemark: String? = null,
    val goodsList: List<OrderGoods>? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
) {
    /** 应付/实付金额 */
    val payableAmount: Int
        get() = if (realPrice > 0) realPrice else (price - discountPrice).coerceAtLeast(0)

    /** 商品总件数 */
    val totalCount: Int
        get() = goodsList.orEmpty().sumOf { it.count }
}
