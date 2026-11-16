package com.joker.coolmallkmp.feature.order.view.screen

import com.joker.coolmallkmp.feature.order.viewmodel.OrderDetailViewModel
import com.joker.coolmallkmp.feature.order.viewmodel.*
import com.joker.coolmallkmp.feature.order.view.skeleton.OrderDetailLoadingSkeleton

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import com.joker.coolmallkmp.core.ui.component.AppButtonBordered
import com.joker.coolmallkmp.core.ui.component.AppButtonFixed
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmallkmp.core.ui.component.PriceText
import com.joker.coolmallkmp.core.ui.component.PriceTextSmall
import com.joker.coolmallkmp.core.ui.component.TitleWithLine
import com.joker.coolmallkmp.core.ui.component.AppCard
import com.joker.coolmallkmp.core.ui.component.OrderGoodsItem
import com.joker.coolmallkmp.core.ui.component.RefreshLayout
import com.joker.coolmallkmp.core.ui.theme.ColorDanger
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.feature.main.component.CenterTopAppBar
import com.joker.coolmallkmp.core.model.entity.Address
import com.joker.coolmallkmp.core.model.entity.Order
import com.joker.coolmallkmp.core.model.entity.OrderGoods
import com.joker.coolmallkmp.core.platform.ThirdPartyCapabilities
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.common_goods
import coolmallkmp.shared.generated.resources.common_retry
import coolmallkmp.shared.generated.resources.order_apply_refund
import coolmallkmp.shared.generated.resources.order_cancel
import coolmallkmp.shared.generated.resources.order_confirm_ios_pay_hint
import coolmallkmp.shared.generated.resources.order_confirm_receive
import coolmallkmp.shared.generated.resources.order_detail_amount_section
import coolmallkmp.shared.generated.resources.order_detail_close_remark
import coolmallkmp.shared.generated.resources.order_detail_create_time
import coolmallkmp.shared.generated.resources.order_detail_discount
import coolmallkmp.shared.generated.resources.order_detail_goods_amount
import coolmallkmp.shared.generated.resources.order_detail_no_address
import coolmallkmp.shared.generated.resources.order_detail_order_num
import coolmallkmp.shared.generated.resources.order_detail_pay_time
import coolmallkmp.shared.generated.resources.order_detail_pay_unsupported
import coolmallkmp.shared.generated.resources.order_detail_payable
import coolmallkmp.shared.generated.resources.order_detail_remark
import coolmallkmp.shared.generated.resources.order_detail_title
import coolmallkmp.shared.generated.resources.order_go_comment
import coolmallkmp.shared.generated.resources.order_go_pay
import coolmallkmp.shared.generated.resources.order_view_logistics
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import com.joker.coolmallkmp.core.ui.resolveUiMessageSuspend
import com.joker.coolmallkmp.feature.order.component.statusLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailRoute(
    orderId: Long,
    onBack: () -> Unit,
    onLogisticsClick: (Long) -> Unit = {},
    onRefundClick: (Long) -> Unit = {},
    onCommentClick: (Long) -> Unit = {},
    onPayClick: (orderId: Long, price: Int) -> Unit = { _, _ -> },
    viewModel: OrderDetailViewModel = viewModel(
        key = "order_detail_$orderId",
        factory = viewModelFactory {
            initializer {
                OrderDetailViewModel(
                    orderId = orderId,
                    orderRepository = AppContainerProvider.instance.orderRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isActing by viewModel.isActing.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }

    // 从退款等子页面返回时刷新订单状态（首次进入由 ViewModel.init 加载）
    var skipFirstResume by remember { mutableStateOf(true) }
    LifecycleResumeEffect(Unit) {
        if (skipFirstResume) {
            skipFirstResume = false
        } else {
            viewModel.load()
        }
        onPauseOrDispose { }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.order_detail_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val order = (uiState as? OrderDetailUiState.Success)?.order
            if (order != null) {
                OrderDetailBottomBar(
                    order = order,
                    isActing = isActing,
                    onCancel = { viewModel.cancelOrder() },
                    onConfirmReceive = { viewModel.confirmReceive() },
                    onLogisticsClick = { onLogisticsClick(order.id) },
                    onRefundClick = { onRefundClick(order.id) },
                    onCommentClick = { onCommentClick(order.id) },
                    onPayClick = { onPayClick(order.id, order.payableAmount) },
                )
            }
        },
    ) { padding ->
        when (val state = uiState) {
            OrderDetailUiState.Loading -> {
                OrderDetailLoadingSkeleton(Modifier.padding(padding))
            }

            is OrderDetailUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::load,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }

            is OrderDetailUiState.Success -> {
                OrderDetailContent(
                    order = state.order,
                    onRefresh = viewModel::load,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OrderDetailBottomBar(
    order: Order,
    isActing: Boolean,
    onCancel: () -> Unit,
    onConfirmReceive: () -> Unit,
    onLogisticsClick: () -> Unit = {},
    onRefundClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onPayClick: () -> Unit = {},
) {
    // 与 CoolMallKotlin OrderButtons 对齐的状态可见性
    val showCancel = order.status == 0
    val showPay = order.status == 0
    val showRefund = order.status == 1 || order.status == 2
    val showConfirm = order.status == 2
    val showLogistics = order.status in 2..6
    val showComment = order.status == 3 || order.status == 4

    if (!showCancel && !showPay && !showRefund && !showConfirm &&
        !showLogistics && !showComment
    ) {
        return
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
        if (showPay && !ThirdPartyCapabilities.supportsAlipay) {
            Text(
                text = stringResource(Res.string.order_detail_pay_unsupported),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (showLogistics) {
                AppButtonBordered(
                    text = stringResource(Res.string.order_view_logistics),
                    onClick = onLogisticsClick,
                    enabled = !isActing,
                    size = AppButtonSize.MINI,
                )
            }
            if (showRefund) {
                AppButtonBordered(
                    text = stringResource(Res.string.order_apply_refund),
                    onClick = onRefundClick,
                    enabled = !isActing,
                    size = AppButtonSize.MINI,
                )
            }
            if (showComment) {
                AppButtonBordered(
                    text = stringResource(Res.string.order_go_comment),
                    onClick = onCommentClick,
                    enabled = !isActing,
                    size = AppButtonSize.MINI,
                )
            }
            if (showCancel) {
                AppButtonBordered(
                    text = stringResource(Res.string.order_cancel),
                    onClick = onCancel,
                    enabled = !isActing,
                    size = AppButtonSize.MINI,
                )
            }
            if (showPay && ThirdPartyCapabilities.supportsAlipay) {
                AppButtonFixed(
                    text = stringResource(Res.string.order_go_pay),
                    onClick = onPayClick,
                    enabled = !isActing,
                    size = AppButtonSize.MINI,
                    style = AppButtonStyle.GRADIENT,
                )
            }
            if (showConfirm) {
                AppButtonFixed(
                    text = stringResource(Res.string.order_confirm_receive),
                    onClick = onConfirmReceive,
                    enabled = !isActing,
                    loading = isActing,
                    size = AppButtonSize.MINI,
                    style = AppButtonStyle.GRADIENT,
                )
            }
        }
        }
    }
}

@Composable
private fun OrderDetailContent(
    order: Order,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RefreshLayout(
        modifier = modifier.fillMaxSize(),
        listState = rememberLazyListState(),
        onRefresh = onRefresh,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = order.statusLabel(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(
                            Res.string.order_detail_order_num,
                            order.orderNum.ifBlank { order.id.toString() },
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (!order.createTime.isNullOrBlank()) {
                        Text(
                            text = stringResource(
                                Res.string.order_detail_create_time,
                                order.createTime.orEmpty(),
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (!order.payTime.isNullOrBlank()) {
                        Text(
                            text = stringResource(
                                Res.string.order_detail_pay_time,
                                order.payTime.orEmpty(),
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item {
            AddressBlock(address = order.address)
        }

        items(order.goodsList.orEmpty(), key = { it.id }) { goods ->
            AppCard(modifier = Modifier.fillMaxWidth()) {
                OrderGoodsItem(data = goods)
            }
        }

        item {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    TitleWithLine(text = stringResource(Res.string.order_detail_amount_section))
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(
                        label = stringResource(Res.string.order_detail_goods_amount),
                        price = order.price,
                    )
                    if (order.discountPrice > 0) {
                        PriceRow(
                            label = stringResource(Res.string.order_detail_discount),
                            price = order.discountPrice,
                            negative = true,
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    PriceRow(
                        label = stringResource(Res.string.order_detail_payable),
                        price = order.payableAmount,
                        emphasize = true,
                    )
                    if (!order.remark.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                Res.string.order_detail_remark,
                                order.remark.orEmpty(),
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (!order.closeRemark.isNullOrBlank()) {
                        Text(
                            text = stringResource(
                                Res.string.order_detail_close_remark,
                                order.closeRemark.orEmpty(),
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(Res.string.order_confirm_ios_pay_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AddressBlock(address: Address?) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (address == null || address.id <= 0) {
                    Text(
                        text = stringResource(Res.string.order_detail_no_address),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = address.contact,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = address.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = address.displayLine(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    price: Int,
    emphasize: Boolean = false,
    negative: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        if (negative) {
            Text(
                text = "-",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorDanger,
            )
        }
        PriceText(
            price = price,
            showDecimals = false,
            integerStyle = if (emphasize) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            symbolStyle = MaterialTheme.typography.bodyMedium,
        )
    }
}
