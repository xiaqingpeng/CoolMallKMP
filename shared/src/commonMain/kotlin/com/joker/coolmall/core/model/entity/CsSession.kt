package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class CsSession(
    val id: Long = 0,
    val userId: Long = 0,
    val lastMsg: CsMsg? = null,
    val adminUnreadCount: Long = 0,
    val nickName: String = "",
    val avatarUrl: String = "",
    val createTime: String? = null,
    val updateTime: String? = null,
)
