package com.joker.coolmallkmp.feature.launch.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.joker.coolmallkmp.feature.launch.GuideRoute
import com.joker.coolmallkmp.feature.launch.SplashRoute
import com.joker.coolmallkmp.ui.navigation.Routes

fun NavController.navigateToGuide(fromSettings: Boolean = false) {
    navigate("guide?fromSettings=$fromSettings") {
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
    onBack: () -> Unit,
) {
    composable(
        route = Routes.GUIDE,
        arguments = listOf(
            navArgument("fromSettings") {
                type = NavType.StringType
                defaultValue = "false"
            },
        ),
    ) { entry ->
        GuideRoute(
            fromSettings = entry.arguments?.getString("fromSettings") == "true",
            toMain = toMain,
            onBack = onBack,
        )
    }
}
