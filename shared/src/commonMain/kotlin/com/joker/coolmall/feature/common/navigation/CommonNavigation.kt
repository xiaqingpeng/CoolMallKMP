package com.joker.coolmallkmp.feature.common.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.joker.coolmallkmp.feature.common.view.screen.AboutRoute
import com.joker.coolmallkmp.feature.common.view.screen.ContributorsRoute
import com.joker.coolmallkmp.feature.common.view.screen.DocumentRoute
import com.joker.coolmallkmp.feature.common.view.screen.SettingsRoute
import com.joker.coolmallkmp.feature.common.view.screen.WebRoute
import kotlinx.serialization.Serializable
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.privacy_policy
import coolmallkmp.shared.generated.resources.user_agreement
import org.jetbrains.compose.resources.stringResource

@Serializable
data object AboutRouteArgs

@Serializable
data object SettingsRouteArgs

@Serializable
data object UserAgreementRouteArgs

@Serializable
data object PrivacyPolicyRouteArgs

@Serializable
data object ContributorsRouteArgs

@Serializable
data class WebRouteArgs(
    val url: String,
    val title: String = "",
)

fun NavController.navigateToAbout() {
    navigate(AboutRouteArgs)
}

fun NavController.navigateToSettings() {
    navigate(SettingsRouteArgs)
}

fun NavController.navigateToUserAgreement() {
    navigate(UserAgreementRouteArgs)
}

fun NavController.navigateToPrivacyPolicy() {
    navigate(PrivacyPolicyRouteArgs)
}

fun NavController.navigateToContributors() {
    navigate(ContributorsRouteArgs)
}

fun NavController.navigateToWeb(url: String, title: String = "") {
    navigate(WebRouteArgs(url = url, title = title))
}

fun NavGraphBuilder.aboutScreen(
    onBack: () -> Unit,
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onContributorsClick: () -> Unit = {},
    onWebClick: (String, String) -> Unit = { _, _ -> },
) {
    composable<AboutRouteArgs>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 300),
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 300),
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300),
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300),
            )
        },
    ) {
        AboutRoute(
            onBack = onBack,
            onUserAgreementClick = onUserAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
            onContributorsClick = onContributorsClick,
            onWebClick = onWebClick,
        )
    }
}

fun NavGraphBuilder.contributorsScreen(
    onBack: () -> Unit,
) {
    composable<ContributorsRouteArgs> {
        ContributorsRoute(onBack = onBack)
    }
}

fun NavGraphBuilder.webScreen(
    onBack: () -> Unit,
) {
    composable<WebRouteArgs> { entry ->
        val args = entry.toRoute<WebRouteArgs>()
        WebRoute(
            url = args.url,
            title = args.title,
            onBack = onBack,
        )
    }
}

fun NavGraphBuilder.settingsScreen(
    onBack: () -> Unit,
    onUserAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAboutClick: () -> Unit,
    onAccountSecurityClick: () -> Unit = {},
    onReplayGuideClick: () -> Unit = {},
    onFeedbackClick: () -> Unit = {},
) {
    composable<SettingsRouteArgs> {
        SettingsRoute(
            onBack = onBack,
            onUserAgreementClick = onUserAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
            onAboutClick = onAboutClick,
            onAccountSecurityClick = onAccountSecurityClick,
            onReplayGuideClick = onReplayGuideClick,
            onFeedbackClick = onFeedbackClick,
        )
    }
}

fun NavGraphBuilder.userAgreementScreen(
    onBack: () -> Unit,
) {
    composable<UserAgreementRouteArgs> {
        DocumentRoute(
            title = stringResource(Res.string.user_agreement),
            paramKey = "userAgreement",
            onBack = onBack,
        )
    }
}

fun NavGraphBuilder.privacyPolicyScreen(
    onBack: () -> Unit,
) {
    composable<PrivacyPolicyRouteArgs> {
        DocumentRoute(
            title = stringResource(Res.string.privacy_policy),
            paramKey = "privacyPolicy",
            onBack = onBack,
        )
    }
}
