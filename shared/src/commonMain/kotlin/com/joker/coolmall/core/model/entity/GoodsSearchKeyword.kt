package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class GoodsSearchKeyword(
    val id: Long = 0,
    val name: String = "",
    val sortNum: Int = 0,
    val createTime: String? = null,
    val updateTime: String? = null,
)
