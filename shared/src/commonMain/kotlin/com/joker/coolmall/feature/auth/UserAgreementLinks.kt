package com.joker.coolmall.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.auth_login_agree_prefix
import myapplication.shared.generated.resources.privacy_policy
import myapplication.shared.generated.resources.user_agreement
import org.jetbrains.compose.resources.stringResource

/**
 * 登录/注册页底部用户协议与隐私政策链接。
 */
@Composable
fun UserAgreementLinks(
    prefix: String = stringResource(Res.string.auth_login_agree_prefix),
    onUserAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier,
    centerAlignment: Boolean = true,
) {
    val linkColor = MaterialTheme.colorScheme.primary
    val bodyColor = MaterialTheme.colorScheme.onSurfaceVariant
    val agreementLabel = "《${stringResource(Res.string.user_agreement)}》"
    val privacyLabel = "《${stringResource(Res.string.privacy_policy)}》"
    val andLabel = " 和 "
    val linkStyles = TextLinkStyles(style = SpanStyle(color = linkColor))
    val annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = bodyColor)) {
            append(prefix)
        }
        append(" ")
        withLink(
            LinkAnnotation.Clickable(
                tag = "agreement",
                styles = linkStyles,
                linkInteractionListener = { onUserAgreementClick() },
            )
        ) {
            append(agreementLabel)
        }
        withStyle(SpanStyle(color = bodyColor)) {
            append(andLabel)
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "privacy",
                styles = linkStyles,
                linkInteractionListener = { onPrivacyPolicyClick() },
            )
        ) {
            append(privacyLabel)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = if (centerAlignment) {
            Arrangement.Center
        } else {
            Arrangement.Start
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = annotated,
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = if (centerAlignment) TextAlign.Center else TextAlign.Start,
            ),
        )
    }
}
