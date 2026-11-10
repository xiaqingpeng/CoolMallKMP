package com.joker.coolmall.core.model.request

import com.joker.coolmall.core.model.entity.SelectedGoods
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val data: CreateOrder,
) {
    @Serializable
    data class CreateOrder(
        val remark: String = "",
        val addressId: Long,
        val goodsList: List<SelectedGoods>,
        val couponId: Long? = null,
        val title: String = "",
    )
}
