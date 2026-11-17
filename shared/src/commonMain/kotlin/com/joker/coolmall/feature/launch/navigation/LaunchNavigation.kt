package com.joker.coolmallkmp.feature.launch.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.joker.coolmallkmp.feature.launch.view.screen.GuideRoute
import com.joker.coolmallkmp.feature.launch.view.screen.SplashRoute
import com.joker.coolmallkmp.ui.navigation.Routes
import kotlinx.serialization.Serializable

@Serializable
data class GuideRouteArgs(
    val fromSettings: Boolean = false,
)

fun NavController.navigateToGuide(fromSettings: Boolean = false) {
    navigate(GuideRouteArgs(fromSettings = fromSettings)) {
        launchSingleTop = true
        if (!fromSettings) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }
}

fun NavController.navigateToMain() {
    navigate(Routes.MAIN) {
        launchSingleTop = true
        popUpTo(Routes.SPLASH) { inclusive = true }
    }
}

fun NavController.navigateFromGuideToMain() {
    navigate(Routes.MAIN) {
        launchSingleTop = true
        popUpTo<GuideRouteArgs> { inclusive = true }
    }
}

fun NavGraphBuilder.splashScreen(
    toGuide: () -> Unit,
    toMain: () -> Unit,
) {
    composable(Routes.SPLASH) {
        SplashRoute(toGuide = toGuide, toMain = toMain)
    }
}

fun NavGraphBuilder.guideScreen(
    toMain: () -> Unit,
    onBack: () -> Unit,
) {
    composable<GuideRouteArgs> { entry ->
        val args = entry.toRoute<GuideRouteArgs>()
        GuideRoute(
            fromSettings = args.fromSettings,
            toMain = toMain,
            onBack = onBack,
        )
    }
}
