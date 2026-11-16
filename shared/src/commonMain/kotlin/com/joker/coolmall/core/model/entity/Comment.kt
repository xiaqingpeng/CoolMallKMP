package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Long = 0,
    val userId: Long = 0,
    val goodsId: Long = 0,
    val orderId: Long = 0,
    val content: String = "",
    val starCount: Int = 5,
    val pics: List<String>? = null,
    val nickName: String? = null,
    val avatarUrl: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
)
