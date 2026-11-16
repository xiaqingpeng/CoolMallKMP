package com.joker.coolmallkmp.feature.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joker.coolmallkmp.core.ui.component.AppButton
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.resolveMessageCode
import com.joker.coolmallkmp.core.ui.resolveUiMessage
import com.joker.coolmallkmp.core.ui.resolveUiMessageSuspend
import com.joker.coolmallkmp.feature.auth.component.ImageCaptchaDialog
import com.joker.coolmallkmp.feature.auth.component.AuthCodeField
import com.joker.coolmallkmp.feature.auth.component.AuthPage
import com.joker.coolmallkmp.feature.auth.component.AuthPasswordField
import com.joker.coolmallkmp.feature.auth.component.AuthPhoneField
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.auth_confirm_password
import coolmallkmp.shared.generated.resources.auth_get_code
import coolmallkmp.shared.generated.resources.auth_new_password
import coolmallkmp.shared.generated.resources.auth_phone
import coolmallkmp.shared.generated.resources.auth_reset_submit
import coolmallkmp.shared.generated.resources.auth_reset_title
import coolmallkmp.shared.generated.resources.auth_sms_code
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResetPasswordRoute(
    onBack: () -> Unit,
    viewModel: ResetPasswordViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ResetPasswordViewModel(AppContainerProvider.instance.authRepository)
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            snackbarHostState.showSnackbar(resolveUiMessageSuspend(msg))
        }
    }

    AuthPage(
        title = stringResource(Res.string.auth_reset_title),
        onBack = onBack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
            Text(
                text = resolveMessageCode(MessageCode.ResetPasswordTip),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(30.dp))
            AuthPhoneField(
                value = uiState.phone,
                onValueChange = viewModel::updatePhone,
                placeholder = stringResource(Res.string.auth_phone),
            )
            Spacer(modifier = Modifier.height(30.dp))
            AuthCodeField(
                value = uiState.smsCode,
                onValueChange = viewModel::updateSmsCode,
                placeholder = stringResource(Res.string.auth_sms_code),
                actionLabel = if (uiState.countdown > 0) "${uiState.countdown}s" else stringResource(Res.string.auth_get_code),
                actionEnabled = uiState.canSendCode,
                onAction = viewModel::sendCode,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(30.dp))
            AuthPasswordField(
                value = uiState.newPassword,
                onValueChange = viewModel::updateNewPassword,
                placeholder = stringResource(Res.string.auth_new_password),
            )
            Spacer(modifier = Modifier.height(30.dp))
            AuthPasswordField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::updateConfirmPassword,
                placeholder = stringResource(Res.string.auth_confirm_password),
            )
            uiState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = resolveUiMessage(message),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(
                text = stringResource(Res.string.auth_reset_submit),
                onClick = viewModel::submit,
                enabled = uiState.canSubmit,
                loading = uiState.isLoading,
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = true,
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
