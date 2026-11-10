package com.joker.coolmall.core.model.request

import com.joker.coolmall.core.model.entity.Comment
import kotlinx.serialization.Serializable

/**
 * 商品评论提交请求（对齐 CoolMallKotlin GoodsCommentSubmitRequest）
 * POST goods/comment/submit
 */
@Serializable
data class GoodsCommentSubmitRequest(
    val orderId: Long,
    val data: Comment,
)
