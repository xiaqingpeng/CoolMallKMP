package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class GoodsSpec(
    val id: Long = 0,
    val goodsId: Long = 0,
    val name: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    val sortNum: Int = 0,
    val images: List<String>? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
)
