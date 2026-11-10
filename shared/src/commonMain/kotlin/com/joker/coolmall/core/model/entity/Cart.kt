package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 购物车条目：同一商品可挂多个规格。
 */
@Serializable
data class Cart(
    val goodsId: Long = 0,
    val goodsName: String = "",
    val goodsMainPic: String = "",
    val spec: List<CartGoodsSpec> = emptyList(),
) {
    val totalCount: Int
        get() = spec.sumOf { it.count }

    val totalAmount: Int
        get() = spec.sumOf { it.price * it.count }
}
