package com.joker.coolmallkmp.ui

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
import com.joker.coolmallkmp.core.data.local.ThemeColor
import com.joker.coolmallkmp.core.data.local.ThemeMode
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.network.Network
import com.joker.coolmallkmp.core.ui.theme.coolMallDarkColorScheme
import com.joker.coolmallkmp.core.ui.theme.coolMallLightColorScheme
import com.joker.coolmallkmp.feature.auth.navigation.accountLoginScreen
import com.joker.coolmallkmp.feature.auth.navigation.navigateToAccountLogin
import com.joker.coolmallkmp.feature.auth.navigation.navigateToLogin
import com.joker.coolmallkmp.feature.auth.navigation.loginScreen
import com.joker.coolmallkmp.feature.auth.navigation.navigateToRegister
import com.joker.coolmallkmp.feature.auth.navigation.navigateToResetPassword
import com.joker.coolmallkmp.feature.auth.navigation.navigateToSmsLogin
import com.joker.coolmallkmp.feature.auth.navigation.registerScreen
import com.joker.coolmallkmp.feature.auth.navigation.resetPasswordScreen
import com.joker.coolmallkmp.feature.auth.navigation.smsLoginScreen
import com.joker.coolmallkmp.feature.common.navigation.aboutScreen
import com.joker.coolmallkmp.feature.common.navigation.navigateToAbout
import com.joker.coolmallkmp.feature.common.navigation.navigateToPrivacyPolicy
import com.joker.coolmallkmp.feature.common.navigation.navigateToContributors
import com.joker.coolmallkmp.feature.common.navigation.navigateToSettings
import com.joker.coolmallkmp.feature.common.navigation.navigateToUserAgreement
import com.joker.coolmallkmp.feature.common.navigation.navigateToWeb
import com.joker.coolmallkmp.feature.common.navigation.privacyPolicyScreen
import com.joker.coolmallkmp.feature.common.navigation.contributorsScreen
import com.joker.coolmallkmp.feature.common.navigation.settingsScreen
import com.joker.coolmallkmp.feature.common.navigation.userAgreementScreen
import com.joker.coolmallkmp.feature.common.navigation.webScreen
import com.joker.coolmallkmp.feature.cs.navigation.chatScreen
import com.joker.coolmallkmp.feature.cs.navigation.navigateToChat
import com.joker.coolmallkmp.feature.feedback.navigation.feedbackListScreen
import com.joker.coolmallkmp.feature.feedback.navigation.feedbackSubmitScreen
import com.joker.coolmallkmp.feature.feedback.navigation.navigateToFeedbackList
import com.joker.coolmallkmp.feature.feedback.navigation.navigateToFeedbackSubmit
import com.joker.coolmallkmp.feature.goods.navigation.goodsCategoryScreen
import com.joker.coolmallkmp.feature.goods.navigation.goodsDetailScreen
import com.joker.coolmallkmp.feature.goods.navigation.goodsCommentScreen
import com.joker.coolmallkmp.feature.goods.navigation.goodsSearchScreen
import com.joker.coolmallkmp.feature.goods.navigation.GoodsCategoryRouteArgs
import com.joker.coolmallkmp.feature.goods.navigation.navigateToGoodsCategory
import com.joker.coolmallkmp.feature.goods.navigation.GoodsSearchRouteArgs
import com.joker.coolmallkmp.feature.goods.navigation.navigateToGoodsDetail
import com.joker.coolmallkmp.feature.goods.navigation.navigateToGoodsComments
import com.joker.coolmallkmp.feature.goods.navigation.navigateToGoodsSearch
import com.joker.coolmallkmp.feature.launch.navigation.guideScreen
import com.joker.coolmallkmp.feature.launch.navigation.navigateToGuide
import com.joker.coolmallkmp.feature.launch.navigation.navigateFromGuideToMain
import com.joker.coolmallkmp.feature.launch.navigation.navigateToMain
import com.joker.coolmallkmp.feature.launch.navigation.splashScreen
import com.joker.coolmallkmp.feature.main.navigation.mainScreen
import com.joker.coolmallkmp.feature.market.navigation.couponListScreen
import com.joker.coolmallkmp.feature.market.navigation.navigateToCouponList
import com.joker.coolmallkmp.feature.order.navigation.backToMainFromOrder
import com.joker.coolmallkmp.feature.order.navigation.navigateToOrderComment
import com.joker.coolmallkmp.feature.order.navigation.navigateToOrderConfirm
import com.joker.coolmallkmp.feature.order.navigation.navigateToOrderDetail
import com.joker.coolmallkmp.feature.order.navigation.navigateToOrderList
import com.joker.coolmallkmp.feature.order.navigation.navigateToOrderLogistics
import com.joker.coolmallkmp.feature.order.navigation.navigateToOrderPay
import com.joker.coolmallkmp.feature.order.navigation.navigateToOrderRefund
import com.joker.coolmallkmp.feature.order.navigation.navigateToOrderResult
import com.joker.coolmallkmp.feature.order.navigation.orderCommentScreen
import com.joker.coolmallkmp.feature.order.navigation.orderConfirmScreen
import com.joker.coolmallkmp.feature.order.navigation.orderDetailScreen
import com.joker.coolmallkmp.feature.order.navigation.orderListScreen
import com.joker.coolmallkmp.feature.order.navigation.orderLogisticsScreen
import com.joker.coolmallkmp.feature.order.navigation.orderPayScreen
import com.joker.coolmallkmp.feature.order.navigation.orderRefundScreen
import com.joker.coolmallkmp.feature.order.navigation.orderResultScreen
import com.joker.coolmallkmp.feature.user.navigation.addressEditScreen
import com.joker.coolmallkmp.feature.user.navigation.addressListScreen
import com.joker.coolmallkmp.feature.user.navigation.AddressSelectionStore
import com.joker.coolmallkmp.feature.user.navigation.footprintScreen
import com.joker.coolmallkmp.feature.user.navigation.navigateToAddressEdit
import com.joker.coolmallkmp.feature.user.navigation.navigateToAddressList
import com.joker.coolmallkmp.feature.user.navigation.navigateToFootprint
import com.joker.coolmallkmp.feature.user.navigation.navigateToProfile
import com.joker.coolmallkmp.feature.user.navigation.profileScreen
import com.joker.coolmallkmp.ui.navigation.Routes
import com.joker.coolmallkmp.core.session.AuthSession

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
    val themeColor by AppContainerProvider.instance.themePreferences.themeColor
        .collectAsState(initial = ThemeColor.DEFAULT)
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val primary = when (themeColor) {
        ThemeColor.DEFAULT -> com.joker.coolmallkmp.core.ui.theme.PrimaryDefault
        ThemeColor.PURPLE -> com.joker.coolmallkmp.core.ui.theme.ColorPurple
        ThemeColor.GREEN -> com.joker.coolmallkmp.core.ui.theme.ColorSuccess
        ThemeColor.ORANGE -> androidx.compose.ui.graphics.Color(0xFFFF8A00)
        ThemeColor.RED -> com.joker.coolmallkmp.core.ui.theme.ColorDanger
    }
    val colorScheme = if (darkTheme) {
        coolMallDarkColorScheme(primary)
    } else {
        coolMallLightColorScheme(primary)
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
                toMain = navController::navigateFromGuideToMain,
                onBack = { navController.popBackStack() },
            )
            mainScreen(
                onGoodsClick = navController::navigateToGoodsDetail,
                onCategoryClick = { typeId, title ->
                    navController.navigateToGoodsCategory(
                        typeId = typeId,
                        title = title,
                    )
                },
                onLoginClick = navController::navigateToLogin,
                onProfileClick = {
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToProfile()
                    } else {
                        navController.navigateToLogin()
                    }
                },
                onAddressClick = {
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToAddressList()
                    } else {
                        navController.navigateToLogin()
                    }
                },
                onCheckoutClick = navController::navigateToOrderConfirm,
                onOrderListClick = { tabIndex ->
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToOrderList(tabIndex)
                    } else {
                        navController.navigateToLogin()
                    }
                },
                onSearchClick = navController::navigateToGoodsSearch,
                onGithubClick = {
                    navController.navigateToWeb(
                        url = "https://github.com/Joker-x-dev/CoolMallKotlin",
                        title = "GitHub",
                    )
                },
                onFeaturedClick = {
                    navController.navigateToGoodsCategory(featured = true)
                },
                onCouponClick = {
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToCouponList()
                    } else {
                        navController.navigateToLogin()
                    }
                },
                onFootprintClick = {
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToFootprint()
                    } else {
                        navController.navigateToLogin()
                    }
                },
                onFeedbackClick = {
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToFeedbackList()
                    } else {
                        navController.navigateToLogin()
                    }
                },
                onAboutClick = navController::navigateToAbout,
                onSettingsClick = navController::navigateToSettings,
                onCustomerServiceClick = {
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToChat()
                    } else {
                        navController.navigateToLogin()
                    }
                },
            )
            profileScreen(
                onBack = { navController.popBackStack() },
                onLoggedOut = { navController.popBackStack() },
            )
            loginScreen(
                onBack = { navController.popBackStack() },
                onSmsLoginClick = navController::navigateToSmsLogin,
                onAccountLoginClick = navController::navigateToAccountLogin,
                onUserAgreementClick = navController::navigateToUserAgreement,
                onPrivacyPolicyClick = navController::navigateToPrivacyPolicy,
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
                onAddressSelected = { address ->
                    AddressSelectionStore.publish(address)
                    navController.popBackStack()
                },
            )
            addressEditScreen(
                onBack = { navController.popBackStack() },
            )
            orderConfirmScreen(
                onBack = { navController.popBackStack() },
                onSelectAddress = { navController.navigateToAddressList(isSelectMode = true) },
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
                    navController.navigate(
                        GoodsCategoryRouteArgs(
                            title = keyword,
                            keyword = keyword,
                        ),
                    ) {
                        popUpTo<GoodsSearchRouteArgs> { inclusive = true }
                    }
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
                onContributorsClick = navController::navigateToContributors,
                onWebClick = { url, title -> navController.navigateToWeb(url = url, title = title) },
            )
            settingsScreen(
                onBack = { navController.popBackStack() },
                onUserAgreementClick = navController::navigateToUserAgreement,
                onPrivacyPolicyClick = navController::navigateToPrivacyPolicy,
                onAboutClick = navController::navigateToAbout,
                onAccountSecurityClick = {
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToProfile()
                    } else {
                        navController.navigateToLogin()
                    }
                },
                onReplayGuideClick = { navController.navigateToGuide(fromSettings = true) },
                onFeedbackClick = {
                    if (AuthSession.token.isNotBlank()) {
                        navController.navigateToFeedbackList()
                    } else {
                        navController.navigateToLogin()
                    }
                },
            )
            userAgreementScreen(
                onBack = { navController.popBackStack() },
            )
            privacyPolicyScreen(
                onBack = { navController.popBackStack() },
            )
            contributorsScreen(
                onBack = { navController.popBackStack() },
            )
            webScreen(
                onBack = { navController.popBackStack() },
            )
            goodsCategoryScreen(
                onBack = { navController.popBackStack() },
                onGoodsClick = navController::navigateToGoodsDetail,
                onSearch = { args, keyword ->
                    navController.navigateToGoodsCategory(
                        typeId = args.typeId.takeIf { it > 0 },
                        title = args.title,
                        featured = args.featured,
                        recommend = args.recommend,
                        keyword = keyword,
                    )
                },
            )
            goodsDetailScreen(
                onBack = { navController.popBackStack() },
                onBuyNow = {
                    navController.navigateToOrderConfirm()
                },
                onCommentsClick = navController::navigateToGoodsComments,
            )
            goodsCommentScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
