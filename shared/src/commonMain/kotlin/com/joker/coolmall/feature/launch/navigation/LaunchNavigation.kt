package com.joker.coolmallkmp.feature.launch.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmallkmp.feature.launch.GuideRoute
import com.joker.coolmallkmp.feature.launch.SplashRoute
import com.joker.coolmallkmp.ui.navigation.Routes

fun NavController.navigateToGuide() {
    navigate(Routes.GUIDE) {
        launchSingleTop = true
        popUpTo(Routes.MAIN) { inclusive = true }
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
        popUpTo(Routes.GUIDE) { inclusive = true }
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
) {
    composable(Routes.GUIDE) {
        GuideRoute(toMain = toMain)
    }
}
