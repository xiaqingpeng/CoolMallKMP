package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    val id: Long = 0,
    val description: String = "",
    val path: String = "",
    val pic: String = "",
    val sortNum: Int = 0,
    val status: Int = 1,
    val createTime: String? = null,
    val updateTime: String? = null,
)
