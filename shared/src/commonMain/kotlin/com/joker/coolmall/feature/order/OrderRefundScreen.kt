package com.joker.coolmall.feature.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.core.ui.component.PriceTextSmall
import myapplication.shared.generated.resources.price_times_count
import coil3.compose.AsyncImage
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import com.joker.coolmall.core.model.entity.DictItem
import com.joker.coolmall.core.model.entity.Order
import com.joker.coolmall.core.model.entity.OrderGoods
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_goods
import myapplication.shared.generated.resources.common_retry
import myapplication.shared.generated.resources.order_refund_extra
import myapplication.shared.generated.resources.order_refund_no_preset
import myapplication.shared.generated.resources.order_refund_order_num
import myapplication.shared.generated.resources.order_refund_paid_amount
import myapplication.shared.generated.resources.order_refund_reason
import myapplication.shared.generated.resources.order_refund_reason_other
import myapplication.shared.generated.resources.order_refund_retry_reasons
import myapplication.shared.generated.resources.order_refund_submit
import myapplication.shared.generated.resources.order_refund_title
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.resolveUiMessage
import com.joker.coolmall.core.ui.resolveUiMessageSuspend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRefundRoute(
    orderId: Long,
    onBack: () -> Unit,
    viewModel: OrderRefundViewModel = viewModel(
        key = "order_refund_$orderId",
        factory = viewModelFactory {
            initializer {
                val container = AppContainerProvider.instance
                OrderRefundViewModel(
                    orderId = orderId,
                    orderRepository = container.orderRepository,
                    commonRepository = container.commonRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val reasonsState by viewModel.reasonsState.collectAsState()
    val selectedReason by viewModel.selectedReason.collectAsState()
    val customReason by viewModel.customReason.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }
    LaunchedEffect(Unit) {
        viewModel.submitted.collect { onBack() }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.order_refund_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                AppButton(
                    text = stringResource(Res.string.order_refund_submit),
                    onClick = viewModel::submit,
                    enabled = !isSubmitting,
                    loading = isSubmitting,
                    size = AppButtonSize.MEDIUM,
                    style = AppButtonStyle.GRADIENT,
                    fullWidth = true,
                )
            }
        },
    ) { padding ->
        when (val state = uiState) {
            OrderRefundUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is OrderRefundUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::loadOrder,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }

            is OrderRefundUiState.Success -> {
                OrderRefundContent(
                    order = state.order,
                    reasonsState = reasonsState,
                    selectedReason = selectedReason,
                    customReason = customReason,
                    onSelectReason = viewModel::selectReason,
                    onCustomReasonChange = viewModel::updateCustomReason,
                    onRetryReasons = viewModel::loadReasons,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun OrderRefundContent(
    order: Order,
    reasonsState: RefundReasonsUiState,
    selectedReason: DictItem?,
    customReason: String,
    onSelectReason: (DictItem) -> Unit,
    onCustomReasonChange: (String) -> Unit,
    onRetryReasons: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = stringResource(
                            Res.string.order_refund_order_num,
                            order.orderNum.ifBlank { order.id.toString() },
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(
                            Res.string.order_refund_paid_amount,
                            order.payableAmount,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        items(order.goodsList.orEmpty(), key = { it.id }) { goods ->
            RefundGoodsCard(goods = goods)
        }

        item {
            Text(
                text = stringResource(Res.string.order_refund_reason),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }

        when (val reasons = reasonsState) {
            RefundReasonsUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            is RefundReasonsUiState.Error -> {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = resolveUiMessage(reasons.message),
                            color = MaterialTheme.colorScheme.error,
                        )
                        TextButtonLike(
                            onClick = onRetryReasons,
                            text = stringResource(Res.string.order_refund_retry_reasons),
                        )
                    }
                }
            }

            is RefundReasonsUiState.Success -> {
                if (reasons.reasons.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(Res.string.order_refund_no_preset),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(reasons.reasons, key = { it.id ?: it.name.hashCode().toLong() }) { item ->
                        ReasonRow(
                            item = item,
                            selected = selectedReason?.id == item.id &&
                                selectedReason?.name == item.name,
                            onClick = { onSelectReason(item) },
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = customReason,
                onValueChange = onCustomReasonChange,
                label = { Text(stringResource(Res.string.order_refund_extra)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TextButtonLike(onClick: () -> Unit, text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun ReasonRow(
    item: DictItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = item.name.orEmpty().ifBlank {
                stringResource(Res.string.order_refund_reason_other)
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun RefundGoodsCard(goods: OrderGoods) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = goods.displayPic,
                contentDescription = goods.goodsName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goods.goodsName.ifBlank { stringResource(Res.string.common_goods) },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (goods.specName.isNotBlank()) {
                    Text(
                        text = goods.specName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriceTextSmall(price = goods.price.toInt())
                    Text(
                        text = stringResource(Res.string.price_times_count, goods.count),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
