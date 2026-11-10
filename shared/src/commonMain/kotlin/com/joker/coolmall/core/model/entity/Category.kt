package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long = 0,
    val name: String = "",
    val parentId: Int? = null,
    val sortNum: Int = 0,
    val pic: String? = null,
    val status: Int = 1,
    val createTime: String? = null,
    val updateTime: String? = null,
)
