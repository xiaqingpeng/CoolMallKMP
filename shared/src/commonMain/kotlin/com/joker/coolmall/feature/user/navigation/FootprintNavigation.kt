package com.joker.coolmallkmp.feature.user.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmallkmp.feature.user.FootprintRoute
import kotlinx.serialization.Serializable

@Serializable
data object FootprintRouteArgs

fun NavController.navigateToFootprint() {
    navigate(FootprintRouteArgs)
}

fun NavGraphBuilder.footprintScreen(
    onBack: () -> Unit,
    onGoodsClick: (Long) -> Unit,
) {
    composable<FootprintRouteArgs> {
        FootprintRoute(
            onBack = onBack,
            onGoodsClick = onGoodsClick,
        )
    }
}
