package com.joker.coolmall.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.platform.ThirdPartyCapabilities
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.auth_account_login
import myapplication.shared.generated.resources.auth_ios_no_third_party
import myapplication.shared.generated.resources.auth_login
import myapplication.shared.generated.resources.auth_login_agree_prefix
import myapplication.shared.generated.resources.auth_qq_login
import myapplication.shared.generated.resources.auth_sms_login
import myapplication.shared.generated.resources.auth_third_party_section
import myapplication.shared.generated.resources.auth_wechat_login
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.ic_logo
import myapplication.shared.generated.resources.ic_qq
import myapplication.shared.generated.resources.ic_wechat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRoute(
    onBack: () -> Unit,
    onSmsLoginClick: () -> Unit,
    onAccountLoginClick: () -> Unit,
    onQqLoginClick: () -> Unit,
    onWeChatLoginClick: () -> Unit,
    onUserAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
) {
    val hasThirdParty = ThirdPartyCapabilities.supportsQqLogin ||
        ThirdPartyCapabilities.supportsWeChatLogin
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.auth_login),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 80.dp, bottom = 30.dp)
                    .size(120.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AppButton(
                    text = stringResource(Res.string.auth_sms_login),
                    onClick = onSmsLoginClick,
                    size = AppButtonSize.MEDIUM,
                    style = AppButtonStyle.GRADIENT,
                    fullWidth = true,
                )
                Spacer(Modifier.height(20.dp))
                AppButton(
                    text = stringResource(Res.string.auth_account_login),
                    onClick = onAccountLoginClick,
                    size = AppButtonSize.MEDIUM,
                    style = AppButtonStyle.OUTLINED,
                    fullWidth = true,
                )
                if (hasThirdParty) {
                    Spacer(Modifier.height(48.dp))
                    Text(
                        text = stringResource(Res.string.auth_third_party_section),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                    ) {
                        if (ThirdPartyCapabilities.supportsWeChatLogin) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(onClick = onWeChatLoginClick) {
                                    Image(
                                        painter = painterResource(Res.drawable.ic_wechat),
                                        contentDescription = stringResource(Res.string.auth_wechat_login),
                                        modifier = Modifier.size(32.dp),
                                    )
                                }
                                Text(
                                    stringResource(Res.string.auth_wechat_login),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        if (ThirdPartyCapabilities.supportsQqLogin) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(onClick = onQqLoginClick) {
                                    Image(
                                        painter = painterResource(Res.drawable.ic_qq),
                                        contentDescription = stringResource(Res.string.auth_qq_login),
                                        modifier = Modifier.size(32.dp),
                                    )
                                }
                                Text(
                                    stringResource(Res.string.auth_qq_login),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
                UserAgreementLinks(
                    prefix = stringResource(Res.string.auth_login_agree_prefix),
                    onUserAgreementClick = onUserAgreementClick,
                    onPrivacyPolicyClick = onPrivacyPolicyClick,
                    modifier = Modifier.padding(top = 32.dp),
                )
            }
        }
    }
}
