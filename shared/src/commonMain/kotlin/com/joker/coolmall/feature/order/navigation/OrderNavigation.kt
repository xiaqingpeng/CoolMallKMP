package com.joker.coolmallkmp.feature.order.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.joker.coolmallkmp.feature.order.OrderCommentRoute
import com.joker.coolmallkmp.feature.order.OrderConfirmRoute
import com.joker.coolmallkmp.feature.order.OrderDetailRoute
import com.joker.coolmallkmp.feature.order.OrderListRoute
import com.joker.coolmallkmp.feature.order.OrderLogisticsRoute
import com.joker.coolmallkmp.feature.order.OrderPayRoute
import com.joker.coolmallkmp.feature.order.OrderRefundRoute
import com.joker.coolmallkmp.feature.order.OrderResultScreen
import com.joker.coolmallkmp.ui.navigation.Routes
import kotlinx.serialization.Serializable

@Serializable
data object OrderConfirmRouteArgs

@Serializable
data class OrderResultRouteArgs(
    val orderId: Long = 0,
    val orderNum: String = "",
    val amount: Int = 0,
)

@Serializable
data class OrderListRouteArgs(
    val initialTabIndex: Int = 0,
)

@Serializable
data class OrderDetailRouteArgs(
    val orderId: Long = 0,
)

@Serializable
data class OrderLogisticsRouteArgs(
    val orderId: Long = 0,
)

@Serializable
data class OrderRefundRouteArgs(
    val orderId: Long = 0,
)

@Serializable
data class OrderCommentRouteArgs(
    val orderId: Long = 0,
    val goodsId: Long = 0,
)

@Serializable
data class OrderPayRouteArgs(
    val orderId: Long = 0,
    val price: Int = 0,
    val from: String = "",
)

fun NavController.navigateToOrderConfirm() {
    navigate(OrderConfirmRouteArgs)
}

fun NavController.navigateToOrderResult(
    orderId: Long,
    orderNum: String,
    amount: Int,
) {
    navigate(OrderResultRouteArgs(orderId = orderId, orderNum = orderNum, amount = amount))
}

fun NavController.navigateToOrderList(initialTabIndex: Int = 0) {
    navigate(OrderListRouteArgs(initialTabIndex = initialTabIndex))
}

fun NavController.navigateToOrderDetail(orderId: Long) {
    navigate(OrderDetailRouteArgs(orderId = orderId))
}

fun NavController.navigateToOrderLogistics(orderId: Long) {
    navigate(OrderLogisticsRouteArgs(orderId = orderId))
}

fun NavController.navigateToOrderRefund(orderId: Long) {
    navigate(OrderRefundRouteArgs(orderId = orderId))
}

fun NavController.navigateToOrderComment(orderId: Long, goodsId: Long = 0) {
    navigate(OrderCommentRouteArgs(orderId = orderId, goodsId = goodsId))
}

fun NavController.navigateToOrderPay(
    orderId: Long,
    price: Int,
    from: String = "",
) {
    navigate(OrderPayRouteArgs(orderId = orderId, price = price, from = from))
}

fun NavGraphBuilder.orderConfirmScreen(
    onBack: () -> Unit,
    onSelectAddress: () -> Unit = {},
    onOrderCreated: (orderId: Long, orderNum: String, amount: Int) -> Unit,
) {
    composable<OrderConfirmRouteArgs> {
        OrderConfirmRoute(
            onBack = onBack,
            onSelectAddress = onSelectAddress,
            onOrderCreated = { order ->
                onOrderCreated(order.id, order.orderNum, order.payableAmount)
            },
        )
    }
}

fun NavGraphBuilder.orderResultScreen(
    onBackToMain: () -> Unit,
    onViewOrders: () -> Unit = {},
    onPayClick: (orderId: Long, amount: Int) -> Unit = { _, _ -> },
) {
    composable<OrderResultRouteArgs> { entry ->
        val args = entry.toRoute<OrderResultRouteArgs>()
        OrderResultScreen(
            orderId = args.orderId,
            orderNum = args.orderNum,
            amount = args.amount,
            onBackToMain = onBackToMain,
            onViewOrders = onViewOrders,
            onPayClick = { onPayClick(args.orderId, args.amount) },
        )
    }
}

fun NavGraphBuilder.orderListScreen(
    onBack: () -> Unit,
    onOrderClick: (Long) -> Unit,
    onLogisticsClick: (Long) -> Unit = {},
    onRefundClick: (Long) -> Unit = {},
    onCommentClick: (Long) -> Unit = {},
    onPayClick: (orderId: Long, price: Int) -> Unit = { _, _ -> },
) {
    composable<OrderListRouteArgs> { entry ->
        val args = entry.toRoute<OrderListRouteArgs>()
        OrderListRoute(
            initialTabIndex = args.initialTabIndex,
            onBack = onBack,
            onOrderClick = onOrderClick,
            onLogisticsClick = onLogisticsClick,
            onRefundClick = onRefundClick,
            onCommentClick = onCommentClick,
            onPayClick = onPayClick,
        )
    }
}

fun NavGraphBuilder.orderDetailScreen(
    onBack: () -> Unit,
    onLogisticsClick: (Long) -> Unit = {},
    onRefundClick: (Long) -> Unit = {},
    onCommentClick: (Long) -> Unit = {},
    onPayClick: (orderId: Long, price: Int) -> Unit = { _, _ -> },
) {
    composable<OrderDetailRouteArgs> { entry ->
        val args = entry.toRoute<OrderDetailRouteArgs>()
        OrderDetailRoute(
            orderId = args.orderId,
            onBack = onBack,
            onLogisticsClick = onLogisticsClick,
            onRefundClick = onRefundClick,
            onCommentClick = onCommentClick,
            onPayClick = onPayClick,
        )
    }
}

fun NavGraphBuilder.orderPayScreen(
    onBack: () -> Unit,
    onPayFinished: (orderId: Long, success: Boolean, from: String) -> Unit = { _, _, _ -> onBack() },
) {
    composable<OrderPayRouteArgs> { entry ->
        val args = entry.toRoute<OrderPayRouteArgs>()
        OrderPayRoute(
            orderId = args.orderId,
            price = args.price,
            from = args.from,
            onBack = onBack,
            onPayFinished = { success ->
                onPayFinished(args.orderId, success, args.from)
            },
        )
    }
}

fun NavGraphBuilder.orderLogisticsScreen(
    onBack: () -> Unit,
) {
    composable<OrderLogisticsRouteArgs> { entry ->
        val args = entry.toRoute<OrderLogisticsRouteArgs>()
        OrderLogisticsRoute(
            orderId = args.orderId,
            onBack = onBack,
        )
    }
}

fun NavGraphBuilder.orderRefundScreen(
    onBack: () -> Unit,
) {
    composable<OrderRefundRouteArgs> { entry ->
        val args = entry.toRoute<OrderRefundRouteArgs>()
        OrderRefundRoute(
            orderId = args.orderId,
            onBack = onBack,
        )
    }
}

fun NavGraphBuilder.orderCommentScreen(
    onBack: () -> Unit,
) {
    composable<OrderCommentRouteArgs> { entry ->
        val args = entry.toRoute<OrderCommentRouteArgs>()
        OrderCommentRoute(
            orderId = args.orderId,
            goodsId = args.goodsId,
            onBack = onBack,
        )
    }
}

/**
 * 下单成功后回到 Main（清掉确认页与结果页）。
 */
fun NavController.backToMainFromOrder() {
    popBackStack(Routes.MAIN, inclusive = false)
}
