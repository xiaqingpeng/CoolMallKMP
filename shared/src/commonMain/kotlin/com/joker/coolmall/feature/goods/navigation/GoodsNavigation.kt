package com.joker.coolmall.feature.goods.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.joker.coolmall.feature.goods.GoodsDetailRoute
import kotlinx.serialization.Serializable

@Serializable
data class GoodsDetailRouteArgs(val goodsId: Long)

fun NavController.navigateToGoodsDetail(goodsId: Long) {
    navigate(GoodsDetailRouteArgs(goodsId))
}

fun NavGraphBuilder.goodsDetailScreen(
    onBack: () -> Unit,
    onBuyNow: () -> Unit = {},
) {
    composable<GoodsDetailRouteArgs> { entry ->
        val args = entry.toRoute<GoodsDetailRouteArgs>()
        GoodsDetailRoute(
            goodsId = args.goodsId,
            onBack = onBack,
            onBuyNow = onBuyNow,
        )
    }
}
