package com.joker.coolmall.core.model.request

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

/**
 * 订单分页查询请求（对齐 CoolMallKotlin OrderPageRequest）
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class OrderPageRequest(
    @EncodeDefault
    val page: Int = 1,
    @EncodeDefault
    val size: Int = 10,
    val status: List<Int>? = null,
)
