package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class SelectedGoods(
    val goodsId: Long = 0,
    val goodsInfo: Goods? = null,
    val spec: GoodsSpec? = null,
    val count: Int = 0,
)

fun Cart.toSelectedGoodsList(): List<SelectedGoods> {
    return spec.map { cartSpec ->
        SelectedGoods(
            goodsId = goodsId,
            goodsInfo = Goods(
                id = goodsId,
                title = goodsName,
                mainPic = goodsMainPic,
                price = cartSpec.price,
            ),
            spec = GoodsSpec(
                id = cartSpec.id,
                goodsId = cartSpec.goodsId.takeIf { it > 0 } ?: goodsId,
                name = cartSpec.name,
                price = cartSpec.price,
                stock = cartSpec.stock,
                images = cartSpec.images,
            ),
            count = cartSpec.count,
        )
    }
}

fun List<Cart>.toSelectedGoodsList(): List<SelectedGoods> =
    flatMap { it.toSelectedGoodsList() }
