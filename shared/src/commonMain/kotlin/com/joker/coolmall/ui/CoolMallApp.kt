package com.joker.coolmall.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.joker.coolmall.core.data.local.ThemeMode
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.network.Network
import com.joker.coolmall.core.ui.theme.coolMallDarkColorScheme
import com.joker.coolmall.core.ui.theme.coolMallLightColorScheme
import com.joker.coolmall.feature.auth.navigation.accountLoginScreen
import com.joker.coolmall.feature.auth.navigation.navigateToAccountLogin
import com.joker.coolmall.feature.auth.navigation.navigateToRegister
import com.joker.coolmall.feature.auth.navigation.navigateToResetPassword
import com.joker.coolmall.feature.auth.navigation.navigateToSmsLogin
import com.joker.coolmall.feature.auth.navigation.registerScreen
import com.joker.coolmall.feature.auth.navigation.resetPasswordScreen
import com.joker.coolmall.feature.auth.navigation.smsLoginScreen
import com.joker.coolmall.feature.common.navigation.aboutScreen
import com.joker.coolmall.feature.common.navigation.navigateToAbout
import com.joker.coolmall.feature.common.navigation.navigateToPrivacyPolicy
import com.joker.coolmall.feature.common.navigation.navigateToSettings
import com.joker.coolmall.feature.common.navigation.navigateToUserAgreement
import com.joker.coolmall.feature.common.navigation.privacyPolicyScreen
import com.joker.coolmall.feature.common.navigation.settingsScreen
import com.joker.coolmall.feature.common.navigation.userAgreementScreen
import com.joker.coolmall.feature.cs.navigation.chatScreen
import com.joker.coolmall.feature.cs.navigation.navigateToChat
import com.joker.coolmall.feature.feedback.navigation.feedbackListScreen
import com.joker.coolmall.feature.feedback.navigation.feedbackSubmitScreen
import com.joker.coolmall.feature.feedback.navigation.navigateToFeedbackList
import com.joker.coolmall.feature.feedback.navigation.navigateToFeedbackSubmit
import com.joker.coolmall.feature.goods.navigation.goodsCategoryScreen
import com.joker.coolmall.feature.goods.navigation.goodsDetailScreen
import com.joker.coolmall.feature.goods.navigation.goodsSearchScreen
import com.joker.coolmall.feature.goods.navigation.navigateToGoodsCategory
import com.joker.coolmall.feature.goods.navigation.navigateToGoodsDetail
import com.joker.coolmall.feature.goods.navigation.navigateToGoodsSearch
import com.joker.coolmall.feature.launch.navigation.guideScreen
import com.joker.coolmall.feature.launch.navigation.navigateToGuide
import com.joker.coolmall.feature.launch.navigation.navigateToMain
import com.joker.coolmall.feature.launch.navigation.splashScreen
import com.joker.coolmall.feature.main.navigation.mainScreen
import com.joker.coolmall.feature.market.navigation.couponListScreen
import com.joker.coolmall.feature.market.navigation.navigateToCouponList
import com.joker.coolmall.feature.order.navigation.backToMainFromOrder
import com.joker.coolmall.feature.order.navigation.navigateToOrderComment
import com.joker.coolmall.feature.order.navigation.navigateToOrderConfirm
import com.joker.coolmall.feature.order.navigation.navigateToOrderDetail
import com.joker.coolmall.feature.order.navigation.navigateToOrderList
import com.joker.coolmall.feature.order.navigation.navigateToOrderLogistics
import com.joker.coolmall.feature.order.navigation.navigateToOrderPay
import com.joker.coolmall.feature.order.navigation.navigateToOrderRefund
import com.joker.coolmall.feature.order.navigation.navigateToOrderResult
import com.joker.coolmall.feature.order.navigation.orderCommentScreen
import com.joker.coolmall.feature.order.navigation.orderConfirmScreen
import com.joker.coolmall.feature.order.navigation.orderDetailScreen
import com.joker.coolmall.feature.order.navigation.orderListScreen
import com.joker.coolmall.feature.order.navigation.orderLogisticsScreen
import com.joker.coolmall.feature.order.navigation.orderPayScreen
import com.joker.coolmall.feature.order.navigation.orderRefundScreen
import com.joker.coolmall.feature.order.navigation.orderResultScreen
import com.joker.coolmall.feature.user.navigation.addressEditScreen
import com.joker.coolmall.feature.user.navigation.addressListScreen
import com.joker.coolmall.feature.user.navigation.footprintScreen
import com.joker.coolmall.feature.user.navigation.navigateToAddressEdit
import com.joker.coolmall.feature.user.navigation.navigateToAddressList
import com.joker.coolmall.feature.user.navigation.navigateToFootprint
import com.joker.coolmall.feature.user.navigation.navigateToProfile
import com.joker.coolmall.feature.user.navigation.profileScreen
import com.joker.coolmall.ui.navigation.Routes
import com.joker.coolmall.core.session.AuthSession

/**
 * CoolMall KMP 真正业务入口（对齐 netcloudmusic MyApp）
 * Splash → Guide? → Main → AccountLogin / AddressList / OrderConfirm / Goods*
 */
@Composable
fun CoolMallApp() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = { Network.imageHttpClient }))
            }
            .build()
    }

    val themeMode by AppContainerProvider.instance.themePreferences.themeMode
        .collectAsState(initial = ThemeMode.SYSTEM)
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = if (darkTheme) {
        coolMallDarkColorScheme()
    } else {
        coolMallLightColorScheme()
    }

    MaterialTheme(colorScheme = colorScheme) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            splashScreen(
                toGuide = navController::navigateToGuide,
                toMain = navController::navigateToMain,
            )
            guideScreen(
                toMain = navController::navigateToMain,
            )
            mainScreen(
                onGoodsClick = navController::navigateToGoodsDetail,
                onCategoryClick = { typeId, title ->
                    navController.navigateToGoodsCategory(
                        typeId = typeId,
                        title = title,
                    )
                },
                onLoginClick = navController::navigateToAccountLogin,
                onProfileClick = navController::navigateToProfile,
                onAddressClick = navController::navigateToAddressList,
                onCheckoutClick = navController::navigateToOrderConfirm,
                onOrderListClick = { tabIndex -> navController.navigateToOrderList(tabIndex) },
                onSearchClick = navController::navigateToGoodsSearch,
                onFeaturedClick = {
                    navController.navigateToGoodsCategory(featured = true)
                },
                onCouponClick = navController::navigateToCouponList,
                onFootprintClick = navController::navigateToFootprint,
                onFeedbackClick = navController::navigateToFeedbackList,
                onAboutClick = navController::navigateToAbout,
                onSettingsClick = navController::navigateToSettings,
                onCustomerServiceClick = {
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToChat()
                    } else {
                        navController.navigateToAccountLogin()
                    }
                },
            )
            profileScreen(
                onBack = { navController.popBackStack() },
                onLoggedOut = { navController.popBackStack() },
            )
            accountLoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = { navController.popBackStack() },
                onSmsLoginClick = navController::navigateToSmsLogin,
                onRegisterClick = navController::navigateToRegister,
                onForgotPasswordClick = navController::navigateToResetPassword,
                onUserAgreementClick = navController::navigateToUserAgreement,
                onPrivacyPolicyClick = navController::navigateToPrivacyPolicy,
            )
            smsLoginScreen(
                onBack = { navController.popBackStack() },
                // 登录成功后直接回到 Main，避免残留在密码登录页
                onLoginSuccess = {
                    navController.popBackStack(Routes.MAIN, inclusive = false)
                },
                // 短信登录页是叠在密码登录页之上的，直接回退即可回到密码登录
                onSwitchToPassword = { navController.popBackStack() },
                onRegisterClick = navController::navigateToRegister,
                onUserAgreementClick = navController::navigateToUserAgreement,
                onPrivacyPolicyClick = navController::navigateToPrivacyPolicy,
            )
            registerScreen(
                onBack = { navController.popBackStack() },
                // 注册成功后清掉登录/注册栈，回到 Main
                onRegisterSuccess = {
                    navController.popBackStack(Routes.MAIN, inclusive = false)
                },
                onUserAgreementClick = navController::navigateToUserAgreement,
                onPrivacyPolicyClick = navController::navigateToPrivacyPolicy,
            )
            resetPasswordScreen(
                onBack = { navController.popBackStack() },
            )
            addressListScreen(
                onBack = { navController.popBackStack() },
                onAddClick = { navController.navigateToAddressEdit(0) },
                onEditClick = { id -> navController.navigateToAddressEdit(id) },
            )
            addressEditScreen(
                onBack = { navController.popBackStack() },
            )
            orderConfirmScreen(
                onBack = { navController.popBackStack() },
                onOrderCreated = { orderId, orderNum, amount ->
                    navController.popBackStack()
                    navController.navigateToOrderResult(
                        orderId = orderId,
                        orderNum = orderNum,
                        amount = amount,
                    )
                },
            )
            orderResultScreen(
                onBackToMain = navController::backToMainFromOrder,
                onViewOrders = {
                    navController.navigateToOrderList(0)
                },
                onPayClick = { orderId, amount ->
                    navController.navigateToOrderPay(
                        orderId = orderId,
                        price = amount,
                        from = "confirm",
                    )
                },
            )
            orderListScreen(
                onBack = { navController.popBackStack() },
                onOrderClick = navController::navigateToOrderDetail,
                onLogisticsClick = navController::navigateToOrderLogistics,
                onRefundClick = navController::navigateToOrderRefund,
                onCommentClick = { orderId -> navController.navigateToOrderComment(orderId) },
                onPayClick = { orderId, price ->
                    navController.navigateToOrderPay(
                        orderId = orderId,
                        price = price,
                        from = "list",
                    )
                },
            )
            orderDetailScreen(
                onBack = { navController.popBackStack() },
                onLogisticsClick = navController::navigateToOrderLogistics,
                onRefundClick = navController::navigateToOrderRefund,
                onCommentClick = { orderId -> navController.navigateToOrderComment(orderId) },
                onPayClick = { orderId, price ->
                    navController.navigateToOrderPay(
                        orderId = orderId,
                        price = price,
                        from = "detail",
                    )
                },
            )
            orderPayScreen(
                onBack = { navController.popBackStack() },
                onPayFinished = { orderId, success, from ->
                    when {
                        // 确认订单支付成功：进详情看最新状态
                        from == "confirm" -> {
                            navController.popBackStack()
                            navController.navigateToOrderDetail(orderId)
                        }
                        // 列表/详情返回：依赖 LifecycleResumeEffect 刷新订单状态
                        else -> navController.popBackStack()
                    }
                },
            )
            orderLogisticsScreen(
                onBack = { navController.popBackStack() },
            )
            orderRefundScreen(
                onBack = { navController.popBackStack() },
            )
            orderCommentScreen(
                onBack = { navController.popBackStack() },
            )
            goodsSearchScreen(
                onBack = { navController.popBackStack() },
                onSearch = { keyword ->
                    navController.popBackStack()
                    navController.navigateToGoodsCategory(
                        title = keyword,
                        keyword = keyword,
                    )
                },
            )
            couponListScreen(
                onBack = { navController.popBackStack() },
            )
            footprintScreen(
                onBack = { navController.popBackStack() },
                onGoodsClick = navController::navigateToGoodsDetail,
            )
            feedbackListScreen(
                onBack = { navController.popBackStack() },
                onSubmitClick = navController::navigateToFeedbackSubmit,
            )
            feedbackSubmitScreen(
                onBack = { navController.popBackStack() },
            )
            chatScreen(
                onBack = { navController.popBackStack() },
            )
            aboutScreen(
                onBack = { navController.popBackStack() },
                onUserAgreementClick = navController::navigateToUserAgreement,
                onPrivacyPolicyClick = navController::navigateToPrivacyPolicy,
            )
            settingsScreen(
                onBack = { navController.popBackStack() },
                onUserAgreementClick = navController::navigateToUserAgreement,
                onPrivacyPolicyClick = navController::navigateToPrivacyPolicy,
                onAboutClick = navController::navigateToAbout,
            )
            userAgreementScreen(
                onBack = { navController.popBackStack() },
            )
            privacyPolicyScreen(
                onBack = { navController.popBackStack() },
            )
            goodsCategoryScreen(
                onBack = { navController.popBackStack() },
                onGoodsClick = navController::navigateToGoodsDetail,
            )
            goodsDetailScreen(
                onBack = { navController.popBackStack() },
                onBuyNow = {
                    navController.navigateToOrderConfirm()
                },
            )
        }
    }
}
