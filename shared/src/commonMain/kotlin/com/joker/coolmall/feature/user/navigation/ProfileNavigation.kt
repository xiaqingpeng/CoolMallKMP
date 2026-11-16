package com.joker.coolmallkmp.feature.user.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmallkmp.feature.user.view.screen.ProfileRoute

import kotlinx.serialization.Serializable

@Serializable
data object ProfileRouteArgs

fun NavController.navigateToProfile() {
    navigate(ProfileRouteArgs)
}

fun NavGraphBuilder.profileScreen(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit = onBack,
) {
    composable<ProfileRouteArgs> {
        ProfileRoute(
            onBack = onBack,
            onLoggedOut = onLoggedOut,
        )
    }
}
