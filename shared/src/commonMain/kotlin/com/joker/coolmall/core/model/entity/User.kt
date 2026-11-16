package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    val unionid: String = "",
    val avatarUrl: String? = null,
    val nickName: String? = null,
    val phone: String? = null,
    val gender: Int = 0,
    val status: Int = 1,
    val loginType: String = "0",
    val password: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
)
