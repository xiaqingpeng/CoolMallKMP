package com.joker.coolmallkmp.feature.auth.view.screen
import com.joker.coolmallkmp.feature.auth.component.UserAgreementLinks
import com.joker.coolmallkmp.feature.auth.viewmodel.AccountLoginViewModel

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
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.ui.component.AppButton
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import com.joker.coolmallkmp.feature.main.component.CenterTopAppBar
import com.joker.coolmallkmp.core.ui.resolveUiMessage
import com.joker.coolmallkmp.feature.auth.component.AuthBottomLinks
import com.joker.coolmallkmp.feature.auth.component.AuthPage
import com.joker.coolmallkmp.feature.auth.component.AuthPasswordField
import com.joker.coolmallkmp.feature.auth.component.AuthPhoneField
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.auth_account_login
import coolmallkmp.shared.generated.resources.auth_forgot_password
import coolmallkmp.shared.generated.resources.auth_ios_no_third_party
import coolmallkmp.shared.generated.resources.auth_login
import coolmallkmp.shared.generated.resources.auth_login_agree_prefix
import coolmallkmp.shared.generated.resources.auth_password
import coolmallkmp.shared.generated.resources.auth_phone
import coolmallkmp.shared.generated.resources.auth_qq_login
import coolmallkmp.shared.generated.resources.auth_register
import coolmallkmp.shared.generated.resources.auth_sms_login
import coolmallkmp.shared.generated.resources.auth_third_party_section
import coolmallkmp.shared.generated.resources.auth_wechat_login
import coolmallkmp.shared.generated.resources.auth_welcome_login
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.ic_qq
import coolmallkmp.shared.generated.resources.ic_wechat
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

    AuthPage(
        title = stringResource(Res.string.auth_welcome_login),
        onBack = onBack,
    ) {
            AuthPhoneField(
                value = uiState.phone,
                onValueChange = viewModel::updatePhone,
                placeholder = stringResource(Res.string.auth_phone),
            )
            Spacer(Modifier.height(42.dp))
            AuthPasswordField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                placeholder = stringResource(Res.string.auth_password),
            )
            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(resolveUiMessage(message), color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(12.dp))
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
                enabled = uiState.isLoginEnabled,
                loading = uiState.isLoading,
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = true,
            )
            AuthBottomLinks(
                leftText = stringResource(Res.string.auth_register),
                rightText = stringResource(Res.string.auth_forgot_password),
                onLeftClick = onRegisterClick,
                onRightClick = onForgotPasswordClick,
                divider = true,
            )
            if (showQq || showWeChat) {
                Spacer(Modifier.height(16.dp))
                Text(stringResource(Res.string.auth_third_party_section), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (showQq) {
                        OutlinedButton(onClick = viewModel::loginWithQq, modifier = Modifier.weight(1f)) {
                            Text(stringResource(Res.string.auth_qq_login))
                        }
                    }
                    if (showWeChat) {
                        OutlinedButton(onClick = viewModel::loginWithWeChat, modifier = Modifier.weight(1f)) {
                            Text(stringResource(Res.string.auth_wechat_login))
                        }
                    }
                }
            }
        }
}
