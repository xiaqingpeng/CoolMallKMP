package com.joker.coolmall.feature.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.feature.auth.component.ImageCaptchaDialog
import com.joker.coolmall.feature.auth.component.AuthCodeField
import com.joker.coolmall.feature.auth.component.AuthBottomLinks
import com.joker.coolmall.feature.auth.component.AuthPage
import com.joker.coolmall.feature.auth.component.AuthPasswordField
import com.joker.coolmall.feature.auth.component.AuthPhoneField
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.core.ui.resolveUiMessage
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.auth_confirm_password
import myapplication.shared.generated.resources.auth_get_code
import myapplication.shared.generated.resources.auth_password_min_hint
import myapplication.shared.generated.resources.auth_phone
import myapplication.shared.generated.resources.auth_register_agree_prefix
import myapplication.shared.generated.resources.auth_register_and_login
import myapplication.shared.generated.resources.auth_register_title
import myapplication.shared.generated.resources.auth_have_account
import myapplication.shared.generated.resources.auth_go_login
import myapplication.shared.generated.resources.auth_sms_code
import myapplication.shared.generated.resources.auth_welcome_register
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegisterRoute(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                RegisterViewModel(AppContainerProvider.instance.authRepository)
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.registerSuccess.collect { onRegisterSuccess() }
    }

    AuthPage(
        title = stringResource(Res.string.auth_welcome_register),
        onBack = onBack,
    ) {
            AuthPhoneField(
                value = uiState.phone,
                onValueChange = viewModel::updatePhone,
                placeholder = stringResource(Res.string.auth_phone),
            )
            Spacer(Modifier.height(30.dp))
            AuthCodeField(
                value = uiState.smsCode,
                onValueChange = viewModel::updateSmsCode,
                placeholder = stringResource(Res.string.auth_sms_code),
                actionLabel = if (uiState.countdown > 0) "${uiState.countdown}s" else stringResource(Res.string.auth_get_code),
                actionEnabled = uiState.canSendCode,
                onAction = viewModel::sendSmsCode,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(30.dp))
            AuthPasswordField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                placeholder = stringResource(Res.string.auth_password_min_hint),
            )
            Spacer(Modifier.height(30.dp))
            AuthPasswordField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::updateConfirmPassword,
                placeholder = stringResource(Res.string.auth_confirm_password),
            )
            Spacer(Modifier.height(16.dp))
            UserAgreementLinks(
                prefix = stringResource(Res.string.auth_register_agree_prefix),
                onUserAgreementClick = onUserAgreementClick,
                onPrivacyPolicyClick = onPrivacyPolicyClick,
                centerAlignment = false,
            )
            uiState.errorMessage?.let { error ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = resolveUiMessage(error),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(24.dp))
            AppButton(
                text = stringResource(Res.string.auth_register_and_login),
                onClick = viewModel::register,
                enabled = uiState.canRegister,
                loading = uiState.isLoading,
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = true,
            )
            AuthBottomLinks(
                leftText = stringResource(Res.string.auth_have_account),
                rightText = stringResource(Res.string.auth_go_login),
                onLeftClick = {},
                onRightClick = onBack,
            )
    }
    ImageCaptchaDialog(
        visible = uiState.showCaptchaDialog,
        captcha = uiState.captcha,
        imageCode = uiState.imageCode,
        onImageCodeChange = viewModel::updateImageCode,
        onDismiss = viewModel::dismissCaptcha,
        onConfirm = viewModel::confirmCaptcha,
        onRefresh = viewModel::refreshCaptcha,
    )
}
