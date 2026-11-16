package com.joker.coolmallkmp.feature.order.model

/**
 * 订单状态标签（对齐 CoolMallKotlin OrderStatus）
 *
 * 文案由 UI 层 `stringResource` 解析，避免 enum 硬编码中文。
 *
 * @param status 传给 `order/info/page` 的状态列表，null 表示全部
 */
enum class OrderStatus(
    val status: List<Int>?,
) {
    ALL(null),
    UNPAID(listOf(0)),
    UNSHIPPED(listOf(1)),
    UNRECEIVED(listOf(2)),
    /** 售后/退款：对齐 dump [5, 6] */
    AFTER_SALE(listOf(5, 6)),
    UNEVALUATED(listOf(3)),
    COMPLETED(listOf(4)),
}
