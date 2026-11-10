package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 客服消息
 *
 * type: 0-用户反馈 1-客服回复
 * status: 0-未读 1-已读
 */
@Serializable
data class CsMsg(
    val id: Long = 0,
    val userId: Long = 0,
    val sessionId: Long = 0,
    val content: MessageContent = MessageContent(),
    val type: Int = 0,
    val status: Int = 0,
    val nickName: String = "",
    val avatarUrl: String = "",
    val adminUserName: String = "",
    val adminUserHeadImg: String = "",
    val createTime: String? = null,
    val updateTime: String? = null,
) {
    @Serializable
    data class MessageContent(
        val type: String = "",
        val data: String = "",
    )
}
