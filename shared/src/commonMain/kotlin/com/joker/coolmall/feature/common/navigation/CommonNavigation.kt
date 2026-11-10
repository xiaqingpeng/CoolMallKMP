package com.joker.coolmall.feature.common.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmall.feature.common.AboutRoute
import com.joker.coolmall.feature.common.DocumentRoute
import com.joker.coolmall.feature.common.SettingsRoute
import kotlinx.serialization.Serializable
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.privacy_policy
import myapplication.shared.generated.resources.user_agreement
import org.jetbrains.compose.resources.stringResource

@Serializable
data object AboutRouteArgs

@Serializable
data object SettingsRouteArgs

@Serializable
data object UserAgreementRouteArgs

@Serializable
data object PrivacyPolicyRouteArgs

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

fun NavGraphBuilder.aboutScreen(
    onBack: () -> Unit,
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
) {
    composable<AboutRouteArgs> {
        AboutRoute(
            onBack = onBack,
            onUserAgreementClick = onUserAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
        )
    }
}

fun NavGraphBuilder.settingsScreen(
    onBack: () -> Unit,
    onUserAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    composable<SettingsRouteArgs> {
        SettingsRoute(
            onBack = onBack,
            onUserAgreementClick = onUserAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
            onAboutClick = onAboutClick,
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
