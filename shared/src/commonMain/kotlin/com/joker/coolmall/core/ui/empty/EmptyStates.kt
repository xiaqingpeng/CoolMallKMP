package com.joker.coolmall.core.ui.empty

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage
import com.joker.coolmall.core.ui.resolveUiMessage
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.cart_empty
import myapplication.shared.generated.resources.cart_empty_hint
import myapplication.shared.generated.resources.common_retry
import myapplication.shared.generated.resources.empty_cart_btn
import myapplication.shared.generated.resources.empty_data
import myapplication.shared.generated.resources.empty_data_subtitle
import myapplication.shared.generated.resources.empty_error
import myapplication.shared.generated.resources.empty_error_subtitle
import myapplication.shared.generated.resources.empty_network
import myapplication.shared.generated.resources.empty_network_subtitle
import myapplication.shared.generated.resources.ic_empty_cart
import myapplication.shared.generated.resources.ic_empty_data
import myapplication.shared.generated.resources.ic_empty_error
import myapplication.shared.generated.resources.ic_empty_network
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 通用空态/错误态（对齐 CoolMallKotlin Empty 组件布局：图标 + 主文案 + 副文案 + 可选重试）。
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: Painter,
    retryButtonText: String = stringResource(Res.string.common_retry),
    onRetryClick: (() -> Unit)? = null,
    tintIcon: Boolean = true,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            colorFilter = if (tintIcon) {
                ColorFilter.tint(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            } else {
                null
            },
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (onRetryClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = onRetryClick,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(horizontal = 50.dp)
                    .widthIn(min = 200.dp),
            ) {
                Text(text = retryButtonText)
            }
        }
    }
}

@Composable
fun EmptyState(
    message: StringResource,
    modifier: Modifier = Modifier,
    subtitle: StringResource? = null,
    icon: DrawableResource,
    retryButtonText: StringResource = Res.string.common_retry,
    onRetryClick: (() -> Unit)? = null,
    tintIcon: Boolean = true,
) {
    EmptyState(
        message = stringResource(message),
        modifier = modifier,
        subtitle = subtitle?.let { stringResource(it) },
        icon = painterResource(icon),
        retryButtonText = stringResource(retryButtonText),
        onRetryClick = onRetryClick,
        tintIcon = tintIcon,
    )
}

/** 网络失败 */
@Composable
fun EmptyNetwork(
    modifier: Modifier = Modifier,
    onRetryClick: (() -> Unit)? = null,
) {
    EmptyState(
        message = Res.string.empty_network,
        subtitle = Res.string.empty_network_subtitle,
        icon = Res.drawable.ic_empty_network,
        retryButtonText = Res.string.common_retry,
        onRetryClick = onRetryClick,
        modifier = modifier,
    )
}

/** 加载失败（业务错误） */
@Composable
fun EmptyError(
    modifier: Modifier = Modifier,
    message: String? = null,
    onRetryClick: (() -> Unit)? = null,
) {
    EmptyState(
        message = message ?: stringResource(Res.string.empty_error),
        subtitle = stringResource(Res.string.empty_error_subtitle),
        icon = painterResource(Res.drawable.ic_empty_error),
        retryButtonText = stringResource(Res.string.common_retry),
        onRetryClick = onRetryClick,
        modifier = modifier,
    )
}

/** 暂无数据 */
@Composable
fun EmptyData(
    modifier: Modifier = Modifier,
    message: String? = null,
    subtitle: String? = null,
    onRetryClick: (() -> Unit)? = null,
) {
    EmptyState(
        message = message ?: stringResource(Res.string.empty_data),
        subtitle = subtitle ?: stringResource(Res.string.empty_data_subtitle),
        icon = painterResource(Res.drawable.ic_empty_data),
        retryButtonText = stringResource(Res.string.common_retry),
        onRetryClick = onRetryClick,
        modifier = modifier,
    )
}

/** 购物车为空 */
@Composable
fun EmptyCart(
    modifier: Modifier = Modifier,
    onBrowseClick: (() -> Unit)? = null,
) {
    EmptyState(
        message = Res.string.cart_empty,
        subtitle = Res.string.cart_empty_hint,
        icon = Res.drawable.ic_empty_cart,
        retryButtonText = Res.string.empty_cart_btn,
        onRetryClick = onBrowseClick,
        modifier = modifier,
    )
}

/**
 * 根据 [UiMessage] 选择网络空态或业务错误空态。
 */
@Composable
fun EmptyUiMessage(
    message: UiMessage,
    modifier: Modifier = Modifier,
    onRetryClick: (() -> Unit)? = null,
) {
    val resolved = resolveUiMessage(message)
    val isNetwork = message is UiMessage.Code && message.code == MessageCode.NetworkError
    if (isNetwork) {
        EmptyNetwork(modifier = modifier, onRetryClick = onRetryClick)
    } else {
        EmptyError(modifier = modifier, message = resolved, onRetryClick = onRetryClick)
    }
}
