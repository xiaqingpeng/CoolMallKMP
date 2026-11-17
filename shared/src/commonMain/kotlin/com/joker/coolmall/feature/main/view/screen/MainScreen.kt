package com.joker.coolmallkmp.feature.main.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.ui.theme.PrimaryDefault
import com.joker.coolmallkmp.feature.main.component.PlatformBottomNavIcon
import com.joker.coolmallkmp.feature.main.model.TopLevelDestination
import com.joker.coolmallkmp.feature.main.model.MainTabRequest
import kotlinx.coroutines.launch
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.home_title
import coolmallkmp.shared.generated.resources.category_title
import coolmallkmp.shared.generated.resources.cart_title
import coolmallkmp.shared.generated.resources.me_title
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
    onGithubClick: () -> Unit = {},
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
    val requestedDestination by MainTabRequest.requestedDestination.collectAsState()

    LaunchedEffect(requestedDestination) {
        requestedDestination?.let { destination ->
            pagerState.animateScrollToPage(destination.ordinal)
            MainTabRequest.consume()
        }
    }

    Scaffold(
        // 排除顶部状态栏边距，避免与子页 TopAppBar 双重 inset 导致顶栏被顶下
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(WindowInsets.statusBars),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding(),
            ) {
                TopLevelDestination.entries.forEachIndexed { index, destination ->
                    val selected = pagerState.currentPage == index
                    var isPressed by remember { mutableStateOf(false) }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isPressed = true
                                        tryAwaitRelease()
                                        isPressed = false
                                    },
                                    onTap = {
                                        scope.launch { pagerState.animateScrollToPage(index) }
                                    },
                                )
                            }
                            .padding(vertical = 4.dp),
                    ) {
                        PlatformBottomNavIcon(destination, selected)
                        Text(
                            text = destination.labelText(),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selected) {
                                PrimaryDefault
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            },
                        )
                    }
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
                    onGithubClick = onGithubClick,
                    onAboutClick = onAboutClick,
                    onFeaturedClick = onFeaturedClick,
                    onLoginRequired = onLoginClick,
                )
                TopLevelDestination.CATEGORY -> CategoryRoute(onCategoryClick = onCategoryClick)
                TopLevelDestination.CART -> CartRoute(
                    onCheckoutClick = onCheckoutClick,
                    onLoginRequired = onLoginClick,
                    onGoodsClick = onGoodsClick,
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

