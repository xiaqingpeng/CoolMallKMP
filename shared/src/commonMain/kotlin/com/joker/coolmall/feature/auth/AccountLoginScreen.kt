package com.joker.coolmall.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import com.joker.coolmall.core.ui.resolveUiMessage
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.auth_account_login
import myapplication.shared.generated.resources.auth_forgot_password
import myapplication.shared.generated.resources.auth_ios_no_third_party
import myapplication.shared.generated.resources.auth_login
import myapplication.shared.generated.resources.auth_login_agree_prefix
import myapplication.shared.generated.resources.auth_password
import myapplication.shared.generated.resources.auth_phone
import myapplication.shared.generated.resources.auth_qq_login
import myapplication.shared.generated.resources.auth_register
import myapplication.shared.generated.resources.auth_sms_login
import myapplication.shared.generated.resources.auth_third_party_section
import myapplication.shared.generated.resources.auth_wechat_login
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.ic_qq
import myapplication.shared.generated.resources.ic_wechat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountLoginRoute(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onSmsLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    viewModel: AccountLoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AccountLoginViewModel(AppContainerProvider.instance.authRepository)
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val showQq = viewModel.showQqLogin
    val showWeChat = viewModel.showWeChatLogin

    LaunchedEffect(Unit) {
        viewModel.loginSuccess.collect {
            onLoginSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.auth_account_login),
                showBackIcon = true,
                onBackClick = onBack,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::updatePhone,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.auth_phone)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                enabled = !uiState.isLoading,
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.auth_password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !uiState.isLoading,
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
                text = stringResource(Res.string.auth_login),
                onClick = viewModel::login,
                enabled = uiState.isLoginEnabled,
                loading = uiState.isLoading,
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = true,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(
                    onClick = onSmsLoginClick,
                    enabled = !uiState.isLoading,
                ) {
                    Text(stringResource(Res.string.auth_sms_login))
                }
                TextButton(
                    onClick = onRegisterClick,
                    enabled = !uiState.isLoading,
                ) {
                    Text(stringResource(Res.string.auth_register))
                }
            }
            TextButton(
                onClick = onForgotPasswordClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(Res.string.auth_forgot_password))
            }
            Spacer(modifier = Modifier.height(8.dp))
            UserAgreementLinks(
                prefix = stringResource(Res.string.auth_login_agree_prefix),
                onUserAgreementClick = onUserAgreementClick,
                onPrivacyPolicyClick = onPrivacyPolicyClick,
            )
            if (showQq || showWeChat) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.auth_third_party_section),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (showQq) {
                        OutlinedButton(
                            onClick = viewModel::loginWithQq,
                            enabled = !uiState.isLoading,
                            modifier = Modifier.weight(1f),
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_qq),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(Res.string.auth_qq_login))
                        }
                    }
                    if (showWeChat) {
                        OutlinedButton(
                            onClick = viewModel::loginWithWeChat,
                            enabled = !uiState.isLoading,
                            modifier = Modifier.weight(1f),
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_wechat),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(Res.string.auth_wechat_login))
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.auth_ios_no_third_party),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
