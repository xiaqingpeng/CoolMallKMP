package com.joker.coolmallkmp.core.model.request

import kotlinx.serialization.Serializable

/**
 * 字典数据请求（对齐 CoolMallKotlin DictDataRequest）
 */
@Serializable
data class DictDataRequest(
    val types: List<String>,
)
