package com.joker.coolmallkmp.feature.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.platform.ThirdPartyCapabilities
import com.joker.coolmallkmp.core.ui.component.AppButton
import com.joker.coolmallkmp.core.ui.component.AppButtonBordered
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import com.joker.coolmallkmp.core.ui.component.PriceText
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.order_go_pay
import coolmallkmp.shared.generated.resources.order_result_back_home
import coolmallkmp.shared.generated.resources.order_result_no_pay
import coolmallkmp.shared.generated.resources.order_result_order_id
import coolmallkmp.shared.generated.resources.order_result_order_num
import coolmallkmp.shared.generated.resources.order_result_success
import coolmallkmp.shared.generated.resources.order_result_view_orders
import org.jetbrains.compose.resources.stringResource

/**
 * 下单成功页。Android 可跳转支付宝；iOS 仅提示。
 */
@Composable
fun OrderResultScreen(
    orderId: Long,
    orderNum: String,
    amount: Int,
    onBackToMain: () -> Unit,
    onViewOrders: () -> Unit = {},
    onPayClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(56.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.order_result_success),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.order_result_order_num, orderNum),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(Res.string.order_result_order_id, orderId),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PriceText(
            price = amount,
            showDecimals = false,
            integerStyle = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!ThirdPartyCapabilities.supportsAlipay) {
            Text(
                text = stringResource(Res.string.order_result_no_pay),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (ThirdPartyCapabilities.supportsAlipay) {
            AppButton(
                text = stringResource(Res.string.order_go_pay),
                onClick = onPayClick,
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = true,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppButtonBordered(
                text = stringResource(Res.string.order_result_view_orders),
                onClick = onViewOrders,
                modifier = Modifier.weight(1f),
                size = AppButtonSize.MEDIUM,
                fullWidth = true,
            )
            AppButton(
                text = stringResource(Res.string.order_result_back_home),
                onClick = onBackToMain,
                modifier = Modifier.weight(1f),
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = true,
            )
        }
    }
}
