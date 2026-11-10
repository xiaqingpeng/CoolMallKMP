package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 字典项（对齐 CoolMallKotlin DictItem）
 */
@Serializable
data class DictItem(
    val typeId: Long? = 0,
    val parentId: Long? = 0,
    val name: String? = "",
    val id: Long? = 0,
    val value: Int? = 0,
)
