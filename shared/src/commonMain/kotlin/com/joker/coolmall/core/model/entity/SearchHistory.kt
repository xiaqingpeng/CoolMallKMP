package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 与原项目 SearchHistoryEntity 对齐的跨平台模型。
 * id/time 保留用于未来数据库迁移，当前 DataStore 实现以 keyword 为主键。
 */
@Serializable
data class SearchHistory(
    val id: Long = 0,
    val keyword: String,
    val createTime: Long = 0,
)

typealias SearchHistoryEntity = SearchHistory
