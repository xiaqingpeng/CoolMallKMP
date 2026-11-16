package com.joker.coolmallkmp.feature.order.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.joker.coolmallkmp.navigation.order.OrderRoutes
import com.joker.coolmallkmp.feature.order.view.OrderCommentRoute
import com.joker.coolmallkmp.feature.order.view.OrderConfirmRoute
import com.joker.coolmallkmp.feature.order.view.OrderDetailRoute
import com.joker.coolmallkmp.feature.order.view.OrderListRoute
import com.joker.coolmallkmp.feature.order.view.OrderLogisticsRoute
import com.joker.coolmallkmp.feature.order.view.OrderPayRoute
import com.joker.coolmallkmp.feature.order.view.OrderRefundRoute

/**
 * 订单模块导航图
 *
 * @author Joker.X
 */
fun EntryProviderScope<NavKey>.orderGraph() {
    entry<OrderRoutes.List> { key ->
        OrderListRoute(navKey = key)
    }
    entry<OrderRoutes.Confirm> {
        OrderConfirmRoute()
    }
    entry<OrderRoutes.Detail> { key ->
        OrderDetailRoute(navKey = key)
    }
    entry<OrderRoutes.Pay> { key ->
        OrderPayRoute(navKey = key)
    }
    entry<OrderRoutes.Logistics> { key ->
        OrderLogisticsRoute(navKey = key)
    }
    entry<OrderRoutes.Refund> { key ->
        OrderRefundRoute(navKey = key)
    }
    entry<OrderRoutes.Comment> { key ->
        OrderCommentRoute(navKey = key)
    }
}
