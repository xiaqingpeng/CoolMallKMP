package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 用户足迹（本地 Room 记录）
 */
@Serializable
data class Footprint(
    val goodsId: Long = 0,
    val goodsName: String = "",
    val goodsSubTitle: String? = null,
    val goodsMainPic: String = "",
    val goodsPrice: Int = 0,
    val goodsSold: Int = 0,
    val viewTime: Long = 0,
)
