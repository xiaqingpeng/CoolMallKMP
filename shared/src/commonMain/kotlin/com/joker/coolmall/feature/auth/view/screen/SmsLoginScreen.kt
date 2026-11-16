package com.joker.coolmallkmp.feature.auth.view.screen
import com.joker.coolmallkmp.feature.auth.component.UserAgreementLinks
import com.joker.coolmallkmp.feature.auth.viewmodel.SmsLoginViewModel

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
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.feature.auth.component.ImageCaptchaDialog
import com.joker.coolmallkmp.feature.auth.component.AuthCodeField
import com.joker.coolmallkmp.feature.auth.component.AuthBottomLinks
import com.joker.coolmallkmp.feature.auth.component.AuthPage
import com.joker.coolmallkmp.feature.auth.component.AuthPhoneField
import com.joker.coolmallkmp.core.ui.component.AppButton
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import com.joker.coolmallkmp.core.ui.resolveUiMessage
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.auth_get_code
import coolmallkmp.shared.generated.resources.auth_login
import coolmallkmp.shared.generated.resources.auth_login_agree_prefix
import coolmallkmp.shared.generated.resources.auth_password_login
import coolmallkmp.shared.generated.resources.auth_phone
import coolmallkmp.shared.generated.resources.auth_register
import coolmallkmp.shared.generated.resources.auth_sms_not_available
import coolmallkmp.shared.generated.resources.auth_sms_auto_register_hint
import coolmallkmp.shared.generated.resources.auth_sms_code
import coolmallkmp.shared.generated.resources.auth_sms_title
import coolmallkmp.shared.generated.resources.auth_use_third_party_login
import coolmallkmp.shared.generated.resources.auth_welcome_login
import org.jetbrains.compose.resources.stringResource

@Composable
fun SmsLoginRoute(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onSwitchToPassword: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    viewModel: SmsLoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SmsLoginViewModel(AppContainerProvider.instance.authRepository)
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loginSuccess.collect { onLoginSuccess() }
    }

    AuthPage(
        title = stringResource(Res.string.auth_welcome_login),
        onBack = onBack,
    ) {
            Text(
                text = stringResource(Res.string.auth_sms_auto_register_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(30.dp))
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
            uiState.errorMessage?.let { error ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = resolveUiMessage(error),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(16.dp))
            UserAgreementLinks(
                prefix = stringResource(Res.string.auth_login_agree_prefix),
                onUserAgreementClick = onUserAgreementClick,
                onPrivacyPolicyClick = onPrivacyPolicyClick,
                centerAlignment = false,
            )
            Spacer(Modifier.height(24.dp))
            AppButton(
                text = stringResource(Res.string.auth_login),
                onClick = viewModel::login,
                enabled = uiState.canLogin,
                loading = uiState.isLoading,
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = true,
            )
            AuthBottomLinks(
                leftText = stringResource(Res.string.auth_sms_not_available),
                rightText = stringResource(Res.string.auth_use_third_party_login),
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
