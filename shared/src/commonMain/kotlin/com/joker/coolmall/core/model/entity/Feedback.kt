package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 意见反馈
 */
@Serializable
data class Feedback(
    val id: Long = 0,
    val userId: Long = 0,
    val contact: String = "",
    val type: Int = 0,
    val content: String = "",
    val images: List<String>? = null,
    /** 0-未处理 1-已处理 */
    val status: Int = 0,
    val handlerId: Long? = null,
    val remark: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
)
