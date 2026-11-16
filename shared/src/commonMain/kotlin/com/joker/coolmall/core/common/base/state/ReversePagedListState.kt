package com.joker.coolmall.core.common.base.state

/**
 * 倒序消息列表的分页快照：新消息放在头部，历史页追加到尾部。
 */
data class ReversePagedListState<T>(
    val page: Int = 1,
    val items: List<T> = emptyList(),
    val hasMore: Boolean = true,
    val loading: Boolean = false,
)
