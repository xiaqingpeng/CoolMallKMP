package com.joker.coolmallkmp.feature.market.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.joker.coolmallkmp.navigation.market.MarketRoutes
import com.joker.coolmallkmp.feature.market.view.CouponRoute

/**
 * 营销模块导航图
 *
 * @author Joker.X
 */
fun EntryProviderScope<NavKey>.marketGraph() {
    entry<MarketRoutes.Coupon> {
        CouponRoute()
    }
}
