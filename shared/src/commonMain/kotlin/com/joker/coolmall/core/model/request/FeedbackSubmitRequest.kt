package com.joker.coolmall.core.model.request

import kotlinx.serialization.Serializable

/**
 * 提交意见反馈
 * POST feedback/submit
 */
@Serializable
data class FeedbackSubmitRequest(
    val contact: String? = "",
    val type: Int,
    val content: String,
    val images: List<String>? = null,
)
