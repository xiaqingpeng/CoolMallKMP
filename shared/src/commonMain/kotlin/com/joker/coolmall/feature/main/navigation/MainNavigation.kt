package com.joker.coolmallkmp.feature.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmallkmp.feature.main.view.MainRoute
import com.joker.coolmallkmp.ui.navigation.Routes

fun NavGraphBuilder.mainScreen(
    onGoodsClick: (Long) -> Unit = {},
    onCategoryClick: (Long, String) -> Unit = { _, _ -> },
    onLoginClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {},
    onOrderListClick: (Int) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onGithubClick: () -> Unit = {},
    onFeaturedClick: () -> Unit = {},
    onCouponClick: () -> Unit = {},
    onFootprintClick: () -> Unit = {},
    onFeedbackClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onCustomerServiceClick: () -> Unit = {},
) {
    composable(Routes.MAIN) {
        MainRoute(
            onGoodsClick = onGoodsClick,
            onCategoryClick = onCategoryClick,
            onLoginClick = onLoginClick,
            onProfileClick = onProfileClick,
            onAddressClick = onAddressClick,
            onCheckoutClick = onCheckoutClick,
            onOrderListClick = onOrderListClick,
            onSearchClick = onSearchClick,
            onGithubClick = onGithubClick,
            onFeaturedClick = onFeaturedClick,
            onCouponClick = onCouponClick,
            onFootprintClick = onFootprintClick,
            onAboutClick = onAboutClick,
            onFeedbackClick = onFeedbackClick,
            onSettingsClick = onSettingsClick,
            onCustomerServiceClick = onCustomerServiceClick,
        )
    }
}
