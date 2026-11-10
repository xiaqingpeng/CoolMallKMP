package com.joker.coolmall.core.model.common

import kotlinx.serialization.Serializable

/**
 * ID 模型（对齐 CoolMallKotlin Id）
 */
@Serializable
data class Id(
    val id: Long = 0,
)
