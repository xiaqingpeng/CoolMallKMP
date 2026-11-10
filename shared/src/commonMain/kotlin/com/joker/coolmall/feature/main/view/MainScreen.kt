package com.joker.coolmall.feature.main.view

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.joker.coolmall.feature.main.model.TopLevelDestination
import kotlinx.coroutines.launch
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.home_title
import myapplication.shared.generated.resources.category_title
import myapplication.shared.generated.resources.cart_title
import myapplication.shared.generated.resources.me_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainRoute(
    onGoodsClick: (Long) -> Unit = {},
    onCategoryClick: (Long, String) -> Unit = { _, _ -> },
    onLoginClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {},
    onOrderListClick: (Int) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onFeaturedClick: () -> Unit = {},
    onCouponClick: () -> Unit = {},
    onFootprintClick: () -> Unit = {},
    onFeedbackClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onCustomerServiceClick: () -> Unit = {},
) {
    val pagerState = rememberPagerState { TopLevelDestination.entries.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        // 排除顶部状态栏边距，避免与子页 TopAppBar 双重 inset 导致顶栏被顶下
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(WindowInsets.statusBars),
        bottomBar = {
            NavigationBar {
                TopLevelDestination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon(),
                                contentDescription = destination.labelText(),
                            )
                        },
                        label = { Text(destination.labelText()) },
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            // 底部 Tab 切换即可；关闭横向滑动，避免抢占首页竖直滚动手势
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            beyondViewportPageCount = 1,
        ) { page ->
            when (TopLevelDestination.entries[page]) {
                TopLevelDestination.HOME -> HomeRoute(
                    onGoodsClick = onGoodsClick,
                    onCategoryClick = onCategoryClick,
                    onSearchClick = onSearchClick,
                    onAboutClick = onAboutClick,
                    onFeaturedClick = onFeaturedClick,
                    onLoginRequired = onLoginClick,
                )
                TopLevelDestination.CATEGORY -> CategoryRoute(onCategoryClick = onCategoryClick)
                TopLevelDestination.CART -> CartRoute(
                    onCheckoutClick = onCheckoutClick,
                    onLoginRequired = onLoginClick,
                )
                TopLevelDestination.ME -> MeRoute(
                    onLoginClick = onLoginClick,
                    onProfileClick = onProfileClick,
                    onAddressClick = onAddressClick,
                    onOrderListClick = onOrderListClick,
                    onCouponClick = onCouponClick,
                    onFootprintClick = onFootprintClick,
                    onGoodsClick = onGoodsClick,
                    onFeedbackClick = onFeedbackClick,
                    onAboutClick = onAboutClick,
                    onSettingsClick = onSettingsClick,
                    onCustomerServiceClick = onCustomerServiceClick,
                )
            }
        }
    }
}

@Composable
private fun TopLevelDestination.labelText(): String = when (this) {
    TopLevelDestination.HOME -> stringResource(Res.string.home_title)
    TopLevelDestination.CATEGORY -> stringResource(Res.string.category_title)
    TopLevelDestination.CART -> stringResource(Res.string.cart_title)
    TopLevelDestination.ME -> stringResource(Res.string.me_title)
}

private fun TopLevelDestination.icon(): ImageVector = when (this) {
    TopLevelDestination.HOME -> Icons.Filled.Home
    TopLevelDestination.CATEGORY -> Icons.Outlined.GridView
    TopLevelDestination.CART -> Icons.Filled.ShoppingCart
    TopLevelDestination.ME -> Icons.Filled.Person
}
