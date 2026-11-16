package com.joker.coolmallkmp.feature.cs.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import com.joker.coolmallkmp.feature.cs.view.screen.ChatRoute
import kotlinx.serialization.Serializable

@Serializable
data object ChatRouteArgs

fun NavController.navigateToChat() {
    navigate(ChatRouteArgs)
}

fun NavGraphBuilder.chatScreen(
    onBack: () -> Unit,
) {
    composable<ChatRouteArgs> {
        ChatRoute(onBack = onBack)
    }
}
