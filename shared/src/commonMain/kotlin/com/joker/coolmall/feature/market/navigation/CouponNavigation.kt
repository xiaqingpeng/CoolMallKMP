package com.joker.coolmallkmp.feature.market.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmallkmp.feature.market.CouponListRoute
import kotlinx.serialization.Serializable

@Serializable
data object CouponListRouteArgs

fun NavController.navigateToCouponList() {
    navigate(CouponListRouteArgs)
}

fun NavGraphBuilder.couponListScreen(
    onBack: () -> Unit,
) {
    composable<CouponListRouteArgs> {
        CouponListRoute(onBack = onBack)
    }
}
