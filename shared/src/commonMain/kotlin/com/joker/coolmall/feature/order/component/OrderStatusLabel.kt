package com.joker.coolmallkmp.feature.order.component

import androidx.compose.runtime.Composable
import com.joker.coolmallkmp.core.model.entity.Order
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.order_status_closed
import coolmallkmp.shared.generated.resources.order_status_completed
import coolmallkmp.shared.generated.resources.order_status_pending_comment
import coolmallkmp.shared.generated.resources.order_status_pending_payment
import coolmallkmp.shared.generated.resources.order_status_pending_receipt
import coolmallkmp.shared.generated.resources.order_status_pending_shipment
import coolmallkmp.shared.generated.resources.order_status_refunded
import coolmallkmp.shared.generated.resources.order_status_refunding
import coolmallkmp.shared.generated.resources.order_status_unknown
import org.jetbrains.compose.resources.stringResource

/**
 * 订单状态展示文案（对齐 CoolMallKotlin order_status_*）
 */
@Composable
fun Order.statusLabel(): String = when (status) {
    0 -> stringResource(Res.string.order_status_pending_payment)
    1 -> stringResource(Res.string.order_status_pending_shipment)
    2 -> stringResource(Res.string.order_status_pending_receipt)
    3 -> stringResource(Res.string.order_status_pending_comment)
    4 -> stringResource(Res.string.order_status_completed)
    5 -> stringResource(Res.string.order_status_refunding)
    6 -> stringResource(Res.string.order_status_refunded)
    7 -> stringResource(Res.string.order_status_closed)
    else -> stringResource(Res.string.order_status_unknown)
}
