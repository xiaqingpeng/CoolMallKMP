package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 购物车商品规格（本地结构，不来自服务端）。
 */
@Serializable
data class CartGoodsSpec(
    val id: Long = 0,
    val goodsId: Long = 0,
    val name: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    val count: Int = 0,
    val images: List<String>? = null,
)

fun GoodsSpec.toCartGoodsSpec(count: Int): CartGoodsSpec = CartGoodsSpec(
    id = id,
    goodsId = goodsId,
    name = name,
    price = price,
    stock = stock,
    count = count,
    images = images,
)
