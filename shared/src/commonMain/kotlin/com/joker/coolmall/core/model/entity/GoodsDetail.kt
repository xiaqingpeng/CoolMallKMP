package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class GoodsDetail(
    val goodsInfo: Goods = Goods(),
    val coupon: List<Coupon> = emptyList(),
    val comment: List<Comment> = emptyList(),
)
