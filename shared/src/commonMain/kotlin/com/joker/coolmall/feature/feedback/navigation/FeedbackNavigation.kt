package com.joker.coolmallkmp.feature.feedback.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmallkmp.feature.feedback.FeedbackListRoute
import com.joker.coolmallkmp.feature.feedback.FeedbackSubmitRoute
import kotlinx.serialization.Serializable

@Serializable
data object FeedbackListRouteArgs

@Serializable
data object FeedbackSubmitRouteArgs

fun NavController.navigateToFeedbackList() {
    navigate(FeedbackListRouteArgs)
}

fun NavController.navigateToFeedbackSubmit() {
    navigate(FeedbackSubmitRouteArgs)
}

fun NavGraphBuilder.feedbackListScreen(
    onBack: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    composable<FeedbackListRouteArgs> {
        FeedbackListRoute(
            onBack = onBack,
            onSubmitClick = onSubmitClick,
        )
    }
}

fun NavGraphBuilder.feedbackSubmitScreen(
    onBack: () -> Unit,
) {
    composable<FeedbackSubmitRouteArgs> {
        FeedbackSubmitRoute(onBack = onBack)
    }
}
