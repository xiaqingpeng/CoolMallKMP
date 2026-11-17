package com.joker.coolmallkmp.feature.goods.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmall.feature.goods.view.screen.GoodsSearchRoute
import kotlinx.serialization.Serializable

@Serializable
data object GoodsSearchRouteArgs

fun NavController.navigateToGoodsSearch() {
    navigate(GoodsSearchRouteArgs)
}

fun NavGraphBuilder.goodsSearchScreen(
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
) {
    composable<GoodsSearchRouteArgs> {
        GoodsSearchRoute(
            onBack = onBack,
            onSearch = onSearch,
        )
    }
}
