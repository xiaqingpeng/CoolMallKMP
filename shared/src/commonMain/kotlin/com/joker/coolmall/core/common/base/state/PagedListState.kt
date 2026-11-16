package com.joker.coolmall.core.common.base.state

/**
 * 可复用的分页快照。订单多 Tab 和实时消息列表都可以在自己的外壳中复用，
 * 同时保留各自的请求方向和业务状态。
 */
data class PagedListState<T>(
    val page: Int = 1,
    val items: List<T> = emptyList(),
    val hasMore: Boolean = true,
    val loaded: Boolean = false,
    val loading: Boolean = false,
)
