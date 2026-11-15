package com.joker.coolmall.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.feature.auth.component.ImageCaptchaDialog
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import com.joker.coolmall.core.ui.resolveUiMessage
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.auth_confirm_password
import myapplication.shared.generated.resources.auth_get_code
import myapplication.shared.generated.resources.auth_password_min_hint
import myapplication.shared.generated.resources.auth_phone
import myapplication.shared.generated.resources.auth_register_agree_prefix
import myapplication.shared.generated.resources.auth_register_and_login
import myapplication.shared.generated.resources.auth_register_title
import myapplication.shared.generated.resources.auth_sms_code
import myapplication.shared.generated.resources.common_back
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.auth_register_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::updatePhone,
                label = { Text(stringResource(Res.string.auth_phone)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = uiState.smsCode,
                    onValueChange = viewModel::updateSmsCode,
                    label = { Text(stringResource(Res.string.auth_sms_code)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(
                    onClick = viewModel::sendSmsCode,
                    enabled = uiState.canSendCode,
                ) {
                    if (uiState.isSendingCode) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = if (uiState.countdown > 0) {
                                "${uiState.countdown}s"
                            } else {
                                stringResource(Res.string.auth_get_code)
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = { Text(stringResource(Res.string.auth_password_min_hint)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::updateConfirmPassword,
                label = { Text(stringResource(Res.string.auth_confirm_password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            uiState.errorMessage?.let { error ->
                Text(
                    text = resolveUiMessage(error),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            AppButton(
                text = stringResource(Res.string.auth_register_and_login),
                onClick = viewModel::register,
                enabled = uiState.canRegister,
                loading = uiState.isLoading,
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = true,
            )

            UserAgreementLinks(
                prefix = stringResource(Res.string.auth_register_agree_prefix),
                onUserAgreementClick = onUserAgreementClick,
                onPrivacyPolicyClick = onPrivacyPolicyClick,
            )
        }
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
