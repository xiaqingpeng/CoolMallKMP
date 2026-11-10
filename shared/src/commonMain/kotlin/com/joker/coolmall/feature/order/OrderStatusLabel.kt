package com.joker.coolmall.feature.order

import androidx.compose.runtime.Composable
import com.joker.coolmall.core.model.entity.Order
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.order_status_closed
import myapplication.shared.generated.resources.order_status_completed
import myapplication.shared.generated.resources.order_status_pending_comment
import myapplication.shared.generated.resources.order_status_pending_payment
import myapplication.shared.generated.resources.order_status_pending_receipt
import myapplication.shared.generated.resources.order_status_pending_shipment
import myapplication.shared.generated.resources.order_status_refunded
import myapplication.shared.generated.resources.order_status_refunding
import myapplication.shared.generated.resources.order_status_unknown
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
