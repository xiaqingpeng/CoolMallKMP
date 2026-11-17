package com.joker.coolmallkmp.feature.goods.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.joker.coolmallkmp.feature.goods.view.screen.GoodsDetailRoute
import com.joker.coolmallkmp.feature.goods.view.screen.GoodsCommentRoute
import kotlinx.serialization.Serializable

@Serializable
data class GoodsDetailRouteArgs(val goodsId: Long)

fun NavController.navigateToGoodsDetail(goodsId: Long) {
    navigate(GoodsDetailRouteArgs(goodsId))
}

@Serializable
data class GoodsCommentRouteArgs(val goodsId: Long)

fun NavController.navigateToGoodsComments(goodsId: Long) {
    navigate(GoodsCommentRouteArgs(goodsId))
}

fun NavGraphBuilder.goodsDetailScreen(
    onBack: () -> Unit,
    onBuyNow: () -> Unit = {},
    onCommentsClick: (Long) -> Unit = {},
    onLoginRequired: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
) {
    composable<GoodsDetailRouteArgs> { entry ->
        val args = entry.toRoute<GoodsDetailRouteArgs>()
        GoodsDetailRoute(
            goodsId = args.goodsId,
            onBack = onBack,
            onBuyNow = onBuyNow,
            onCommentsClick = onCommentsClick,
            onLoginRequired = onLoginRequired,
            onChatClick = onChatClick,
            onCartClick = onCartClick,
        )
    }
}

fun NavGraphBuilder.goodsCommentScreen(
    onBack: () -> Unit,
) {
    composable<GoodsCommentRouteArgs> { entry ->
        val args = entry.toRoute<GoodsCommentRouteArgs>()
        GoodsCommentRoute(goodsId = args.goodsId, onBack = onBack)
    }
}
