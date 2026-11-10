package com.joker.coolmall.feature.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.platform.ThirdPartyCapabilities
import com.joker.coolmall.getPlatform
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.about_alipay
import myapplication.shared.generated.resources.about_build
import myapplication.shared.generated.resources.about_capabilities
import myapplication.shared.generated.resources.about_ios_note
import myapplication.shared.generated.resources.about_platform
import myapplication.shared.generated.resources.about_qq_login
import myapplication.shared.generated.resources.about_subtitle
import myapplication.shared.generated.resources.about_tech_detail
import myapplication.shared.generated.resources.about_tech_stack
import myapplication.shared.generated.resources.about_version
import myapplication.shared.generated.resources.about_wechat_login
import myapplication.shared.generated.resources.about_wechat_pay
import myapplication.shared.generated.resources.app_name
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_no
import myapplication.shared.generated.resources.common_yes
import myapplication.shared.generated.resources.me_about
import myapplication.shared.generated.resources.privacy_policy
import myapplication.shared.generated.resources.user_agreement
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutRoute(
    onBack: () -> Unit,
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.me_about)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.about_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(
                        stringResource(Res.string.about_version),
                        AppContainerProvider.versionName.ifBlank { "—" },
                    )
                    InfoRow(
                        stringResource(Res.string.about_build),
                        AppContainerProvider.versionCode.ifBlank { "—" },
                    )
                    InfoRow(
                        stringResource(Res.string.about_platform),
                        getPlatform().name,
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(Res.string.about_capabilities),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(
                        stringResource(Res.string.about_qq_login),
                        yesNoLabel(ThirdPartyCapabilities.supportsQqLogin),
                    )
                    InfoRow(
                        stringResource(Res.string.about_wechat_login),
                        yesNoLabel(ThirdPartyCapabilities.supportsWeChatLogin),
                    )
                    InfoRow(
                        stringResource(Res.string.about_alipay),
                        yesNoLabel(ThirdPartyCapabilities.supportsAlipay),
                    )
                    InfoRow(
                        stringResource(Res.string.about_wechat_pay),
                        yesNoLabel(ThirdPartyCapabilities.supportsWeChatPay),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.about_ios_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(Res.string.about_tech_stack),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.about_tech_detail),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    AboutLinkRow(
                        title = stringResource(Res.string.user_agreement),
                        onClick = onUserAgreementClick,
                    )
                    AboutLinkRow(
                        title = stringResource(Res.string.privacy_policy),
                        onClick = onPrivacyPolicyClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutLinkRow(
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun yesNoLabel(value: Boolean): String =
    if (value) stringResource(Res.string.common_yes) else stringResource(Res.string.common_no)
