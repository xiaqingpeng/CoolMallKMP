package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 订单商品（对齐 CoolMallKotlin OrderGoods）
 */
@Serializable
data class OrderGoods(
    val id: Long = 0,
    val orderId: Long = 0,
    val goodsId: Long = 0,
    val price: Double = 0.0,
    val discountPrice: Double = 0.0,
    val count: Int = 0,
    val remark: String? = null,
    val goodsInfo: Goods? = null,
    val spec: GoodsSpec? = null,
    val isComment: Int = 0,
    val createTime: String? = null,
    val updateTime: String? = null,
) {
    /** 商品名 */
    val goodsName: String
        get() = goodsInfo?.title.orEmpty()

    /** 展示图：规格图 > 商品主图 */
    val displayPic: String
        get() = spec?.images?.firstOrNull()?.takeIf { it.isNotBlank() }
            ?: goodsInfo?.mainPic.orEmpty()

    /** 规格名 */
    val specName: String
        get() = spec?.name.orEmpty()
}
