package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 物流轨迹项（对齐 CoolMallKotlin LogisticsItem）
 */
@Serializable
data class LogisticsItem(
    val time: String? = null,
    val status: String? = null,
)

/**
 * 物流信息（对齐 CoolMallKotlin Logistics）
 */
@Serializable
data class Logistics(
    val number: String? = null,
    val type: String? = null,
    val list: List<LogisticsItem>? = null,
    val deliverystatus: String? = null,
    val issign: String? = null,
    val expName: String? = null,
    val expSite: String? = null,
    val expPhone: String? = null,
    val logo: String? = null,
    val courier: String? = null,
    val courierPhone: String? = null,
    val updateTime: String? = null,
    val takeTime: String? = null,
)
