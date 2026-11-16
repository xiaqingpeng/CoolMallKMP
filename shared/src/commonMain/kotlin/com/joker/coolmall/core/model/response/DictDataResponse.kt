package com.joker.coolmallkmp.core.model.response

import com.joker.coolmallkmp.core.model.entity.DictItem
import kotlinx.serialization.Serializable

/**
 * 字典数据响应（对齐 CoolMallKotlin DictDataResponse）
 */
@Serializable
data class DictDataResponse(
    val orderCancelReason: List<DictItem>? = null,
    val orderRefundReason: List<DictItem>? = null,
    val feedbackType: List<DictItem>? = null,
)
