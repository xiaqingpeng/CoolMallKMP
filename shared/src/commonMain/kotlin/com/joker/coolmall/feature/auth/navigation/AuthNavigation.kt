package com.joker.coolmall.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmall.feature.auth.AccountLoginRoute
import com.joker.coolmall.feature.auth.LoginRoute
import com.joker.coolmall.feature.auth.RegisterRoute
import com.joker.coolmall.feature.auth.ResetPasswordRoute
import com.joker.coolmall.feature.auth.SmsLoginRoute
import kotlinx.serialization.Serializable

@Serializable
data object LoginRouteArgs

@Serializable
data object AccountLoginRouteArgs

@Serializable
data object SmsLoginRouteArgs

@Serializable
data object RegisterRouteArgs

@Serializable
data object ResetPasswordRouteArgs

fun NavController.navigateToLogin() {
    navigate(LoginRouteArgs)
}

fun NavController.navigateToAccountLogin() {
    navigate(AccountLoginRouteArgs)
}

fun NavGraphBuilder.loginScreen(
    onBack: () -> Unit,
    onSmsLoginClick: () -> Unit,
    onAccountLoginClick: () -> Unit,
    onQqLoginClick: () -> Unit = {},
    onWeChatLoginClick: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
) {
    composable<LoginRouteArgs> {
        LoginRoute(
            onBack = onBack,
            onSmsLoginClick = onSmsLoginClick,
            onAccountLoginClick = onAccountLoginClick,
            onQqLoginClick = onQqLoginClick,
            onWeChatLoginClick = onWeChatLoginClick,
            onUserAgreementClick = onUserAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
        )
    }
}

fun NavController.navigateToSmsLogin() {
    navigate(SmsLoginRouteArgs)
}

fun NavController.navigateToRegister() {
    navigate(RegisterRouteArgs)
}

fun NavController.navigateToResetPassword() {
    navigate(ResetPasswordRouteArgs)
}

fun NavGraphBuilder.accountLoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onSmsLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
) {
    composable<AccountLoginRouteArgs> {
        AccountLoginRoute(
            onBack = onBack,
            onLoginSuccess = onLoginSuccess,
            onSmsLoginClick = onSmsLoginClick,
            onRegisterClick = onRegisterClick,
            onForgotPasswordClick = onForgotPasswordClick,
            onUserAgreementClick = onUserAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
        )
    }
}

fun NavGraphBuilder.smsLoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onSwitchToPassword: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
) {
    composable<SmsLoginRouteArgs> {
        SmsLoginRoute(
            onBack = onBack,
            onLoginSuccess = onLoginSuccess,
            onSwitchToPassword = onSwitchToPassword,
            onRegisterClick = onRegisterClick,
            onUserAgreementClick = onUserAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
        )
    }
}

fun NavGraphBuilder.registerScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
) {
    composable<RegisterRouteArgs> {
        RegisterRoute(
            onBack = onBack,
            onRegisterSuccess = onRegisterSuccess,
            onUserAgreementClick = onUserAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
        )
    }
}

fun NavGraphBuilder.resetPasswordScreen(
    onBack: () -> Unit,
) {
    composable<ResetPasswordRouteArgs> {
        ResetPasswordRoute(onBack = onBack)
    }
}
