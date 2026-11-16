package com.joker.coolmallkmp.core.model.common

import kotlinx.serialization.Serializable

/**
 * 批量删除请求（对齐 CoolMallKotlin Ids）
 */
@Serializable
data class Ids(
    val ids: List<Long>,
)
