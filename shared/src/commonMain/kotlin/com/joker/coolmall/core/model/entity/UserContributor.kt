package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserContributor(
    val id: Long = 0,
    val avatarUrl: String = "",
    val nickName: String = "",
    val websiteUrl: String? = null,
    val signature: String? = null,
    val tags: List<String>? = null,
    val sortNum: Int = 0,
    val status: Int = 1,
    val createTime: String? = null,
    val updateTime: String? = null,
)
