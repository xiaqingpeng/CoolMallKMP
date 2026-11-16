package com.joker.coolmallkmp.feature.order

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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmallkmp.core.ui.component.AppButton
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import com.joker.coolmallkmp.core.ui.component.PriceTextLarge
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.platform.ThirdPartyCapabilities
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.ic_alipay
import coolmallkmp.shared.generated.resources.ic_wechat
import coolmallkmp.shared.generated.resources.pay_amount_label
import coolmallkmp.shared.generated.resources.pay_cancel
import coolmallkmp.shared.generated.resources.pay_failed
import coolmallkmp.shared.generated.resources.pay_failed_with_code
import coolmallkmp.shared.generated.resources.pay_method_alipay
import coolmallkmp.shared.generated.resources.pay_method_wechat_disabled
import coolmallkmp.shared.generated.resources.pay_network_error
import coolmallkmp.shared.generated.resources.pay_now
import org.jetbrains.compose.resources.painterResource
import coolmallkmp.shared.generated.resources.pay_order_label
import coolmallkmp.shared.generated.resources.pay_params_failed
import coolmallkmp.shared.generated.resources.pay_success
import coolmallkmp.shared.generated.resources.pay_title
import coolmallkmp.shared.generated.resources.pay_unsupported
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderPayRoute(
    orderId: Long,
    price: Int,
    from: String = "",
    onBack: () -> Unit,
    onPayFinished: (success: Boolean) -> Unit = { onBack() },
    viewModel: OrderPayViewModel = viewModel(
        key = "order_pay_$orderId",
        factory = viewModelFactory {
            initializer {
                OrderPayViewModel(
                    orderId = orderId,
                    orderRepository = AppContainerProvider.instance.orderRepository,
                )
            }
        }
    ),
) {
    val isPaying by viewModel.isPaying.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is OrderPayEvent.ToastCode -> {
                    val message = when (event.code) {
                        PayToastCode.Unsupported -> getString(Res.string.pay_unsupported)
                        PayToastCode.ParamsFailed -> getString(Res.string.pay_params_failed)
                        PayToastCode.Success -> getString(Res.string.pay_success)
                        PayToastCode.Cancel -> getString(Res.string.pay_cancel)
                        PayToastCode.Failed -> {
                            val arg = event.arg
                            if (arg.isNullOrBlank()) {
                                getString(Res.string.pay_failed)
                            } else {
                                getString(Res.string.pay_failed_with_code, arg)
                            }
                        }
                        PayToastCode.NetworkError -> getString(Res.string.pay_network_error)
                    }
                    snackbarHostState.showSnackbar(message)
                }
                is OrderPayEvent.ToastMessage -> snackbarHostState.showSnackbar(event.message)
                is OrderPayEvent.Finished -> onPayFinished(event.success)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.pay_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isPaying) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(Res.string.pay_amount_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceTextLarge(price = price)
                    if (from.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(Res.string.pay_order_label, orderId.toString()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    if (ThirdPartyCapabilities.supportsAlipay) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_alipay),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(Res.string.pay_method_alipay),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            RadioButton(selected = true, onClick = null)
                        }
                    } else {
                        Text(
                            text = stringResource(Res.string.pay_unsupported),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                    // 微信支付：双端都不做完整接入；仅占位说明
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_wechat),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(Res.string.pay_method_wechat_disabled),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        RadioButton(selected = false, onClick = null, enabled = false)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                text = stringResource(Res.string.pay_now),
                onClick = viewModel::startAlipayPayment,
                enabled = ThirdPartyCapabilities.supportsAlipay && !isPaying,
                loading = isPaying,
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = true,
            )
        }
    }
}
