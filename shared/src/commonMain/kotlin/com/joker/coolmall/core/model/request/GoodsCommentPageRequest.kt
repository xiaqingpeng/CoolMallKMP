package com.joker.coolmall.core.model.request

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class GoodsCommentPageRequest(
    val goodsId: String,
    @EncodeDefault val page: Int = 1,
    @EncodeDefault val size: Int = 20,
)
