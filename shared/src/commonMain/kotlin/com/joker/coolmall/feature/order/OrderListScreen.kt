package com.joker.coolmall.feature.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import com.joker.coolmall.core.ui.component.AppButtonFixed
import com.joker.coolmall.core.ui.component.AppButtonBordered
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import com.joker.coolmall.core.model.entity.Order
import com.joker.coolmall.core.platform.ThirdPartyCapabilities
import com.joker.coolmall.feature.order.model.OrderStatus
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_retry
import myapplication.shared.generated.resources.order_apply_refund
import myapplication.shared.generated.resources.order_cancel
import myapplication.shared.generated.resources.order_confirm_receive
import myapplication.shared.generated.resources.order_empty
import myapplication.shared.generated.resources.order_go_comment
import myapplication.shared.generated.resources.order_go_pay
import myapplication.shared.generated.resources.order_list_fallback_num
import myapplication.shared.generated.resources.order_list_goods_info
import myapplication.shared.generated.resources.order_list_item_count
import myapplication.shared.generated.resources.order_list_no_more
import myapplication.shared.generated.resources.order_list_title
import myapplication.shared.generated.resources.order_status_all
import myapplication.shared.generated.resources.order_status_after_sale
import myapplication.shared.generated.resources.order_status_completed_tab
import myapplication.shared.generated.resources.order_status_unevaluated
import myapplication.shared.generated.resources.order_status_unpaid
import myapplication.shared.generated.resources.order_status_unreceived
import myapplication.shared.generated.resources.order_status_unshipped
import myapplication.shared.generated.resources.order_view_detail
import myapplication.shared.generated.resources.order_view_logistics
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.component.PriceText
import com.joker.coolmall.core.ui.component.OrderGoodsItem
import com.joker.coolmall.core.ui.component.AppCard
import com.joker.coolmall.core.ui.component.LoadMore
import com.joker.coolmall.core.common.base.state.LoadMoreState
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.empty.EmptyData
import com.joker.coolmall.core.ui.resolveUiMessageSuspend
import com.joker.coolmall.core.ui.theme.SpaceHorizontalSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListRoute(
    initialTabIndex: Int = 0,
    onBack: () -> Unit,
    onOrderClick: (Long) -> Unit,
    onLogisticsClick: (Long) -> Unit = {},
    onRefundClick: (Long) -> Unit = {},
    onCommentClick: (Long) -> Unit = {},
    onPayClick: (orderId: Long, price: Int) -> Unit = { _, _ -> },
    viewModel: OrderListViewModel = viewModel(
        key = "order_list_$initialTabIndex",
        factory = viewModelFactory {
            initializer {
                OrderListViewModel(
                    orderRepository = AppContainerProvider.instance.orderRepository,
                    initialTabIndex = initialTabIndex,
                )
            }
        }
    ),
) {
    val selectedTab by viewModel.selectedTabIndex.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 从支付/详情/退款等页返回时刷新，避免待付款列表状态陈旧
    var skipFirstResume by remember { mutableStateOf(true) }
    LifecycleResumeEffect(Unit) {
        if (skipFirstResume) {
            skipFirstResume = false
        } else {
            viewModel.refresh()
        }
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.order_list_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = SpaceHorizontalSmall,
            ) {
                OrderStatus.entries.forEachIndexed { index, status ->
                    val selected = selectedTab == index
                    Tab(
                        selected = selected,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = status.tabLabel(),
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        },
                    )
                }
            }

            OrderTabPage(
                tabIndex = selectedTab,
                viewModel = viewModel,
                isLoadingMore = isLoadingMore,
                onOrderClick = onOrderClick,
                onLogisticsClick = onLogisticsClick,
                onRefundClick = onRefundClick,
                onCommentClick = onCommentClick,
                onPayClick = onPayClick,
            )
        }
    }
}

@Composable
private fun OrderTabPage(
    tabIndex: Int,
    viewModel: OrderListViewModel,
    isLoadingMore: Boolean,
    onOrderClick: (Long) -> Unit,
    onLogisticsClick: (Long) -> Unit,
    onRefundClick: (Long) -> Unit,
    onCommentClick: (Long) -> Unit,
    onPayClick: (orderId: Long, price: Int) -> Unit,
) {
    val uiState by viewModel.uiStateFor(tabIndex).collectAsState()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore, uiState, tabIndex) {
        val success = uiState as? OrderListUiState.Success
        if (shouldLoadMore && success?.hasMore == true && !isLoadingMore) {
            viewModel.loadMore(tabIndex)
        }
    }

    when (val state = uiState) {
        OrderListUiState.Loading -> {
            OrderListLoadingSkeleton()
        }

        is OrderListUiState.Error -> {
            EmptyUiMessage(
                message = state.message,
                onRetryClick = { viewModel.refresh(tabIndex) },
                modifier = Modifier.fillMaxSize(),
            )
        }

        is OrderListUiState.Success -> {
            if (state.orders.isEmpty()) {
                EmptyData(
                    message = stringResource(Res.string.order_empty),
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.orders, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            onClick = { onOrderClick(order.id) },
                            onCancel = { viewModel.cancelOrder(order.id) },
                            onConfirmReceive = { viewModel.confirmReceive(order.id) },
                            onLogisticsClick = { onLogisticsClick(order.id) },
                            onRefundClick = { onRefundClick(order.id) },
                            onPayClick = { onPayClick(order.id, order.payableAmount) },
                            onCommentClick = { onCommentClick(order.id) },
                        )
                    }
                    item {
                        LoadMore(
                            state = when {
                                isLoadingMore -> LoadMoreState.Loading
                                !state.hasMore -> LoadMoreState.NoMore
                                else -> LoadMoreState.PullToLoad
                            },
                            onRetry = { viewModel.loadMore(tabIndex) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    onCancel: () -> Unit,
    onConfirmReceive: () -> Unit,
    onLogisticsClick: () -> Unit = {},
    onRefundClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onPayClick: () -> Unit = {},
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = order.orderNum.ifBlank {
                        stringResource(Res.string.order_list_fallback_num, order.id)
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = order.statusLabel(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            val goods = order.goodsList.orEmpty()
            if (goods.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    goods.take(3).forEach { item ->
                        OrderGoodsItem(data = item)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Text(
                    text = order.title?.takeIf { it.isNotBlank() }
                        ?: stringResource(Res.string.order_list_goods_info),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        Res.string.order_list_item_count,
                        order.totalCount.coerceAtLeast(goods.size),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                PriceText(
                    price = order.payableAmount,
                    showDecimals = false,
                    integerStyle = MaterialTheme.typography.titleMedium,
                )
            }

            if (order.createTime != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = order.createTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // 与 CoolMallKotlin OrderButtons 对齐
            val showCancel = order.status == 0
            val showPay = order.status == 0 && ThirdPartyCapabilities.supportsAlipay
            val showRefund = order.status == 1 || order.status == 2
            val showConfirm = order.status == 2
            val showLogistics = order.status in 2..6
            val showComment = order.status == 3 || order.status == 4
            if (showCancel || showPay || showRefund || showConfirm || showLogistics || showComment) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    if (showLogistics) {
                        AppButtonBordered(
                            text = stringResource(Res.string.order_view_logistics),
                            onClick = onLogisticsClick,
                            size = AppButtonSize.MINI,
                        )
                    }
                    if (showRefund) {
                        AppButtonBordered(
                            text = stringResource(Res.string.order_apply_refund),
                            onClick = onRefundClick,
                            size = AppButtonSize.MINI,
                        )
                    }
                    if (showComment) {
                        AppButtonBordered(
                            text = stringResource(Res.string.order_go_comment),
                            onClick = onCommentClick,
                            size = AppButtonSize.MINI,
                        )
                    }
                    if (showCancel) {
                        AppButtonBordered(
                            text = stringResource(Res.string.order_cancel),
                            onClick = onCancel,
                            size = AppButtonSize.MINI,
                        )
                    }
                    if (showPay) {
                        AppButtonFixed(
                            text = stringResource(Res.string.order_go_pay),
                            onClick = onPayClick,
                            size = AppButtonSize.MINI,
                            style = AppButtonStyle.GRADIENT,
                        )
                    }
                    if (showConfirm) {
                        AppButtonFixed(
                            text = stringResource(Res.string.order_confirm_receive),
                            onClick = onConfirmReceive,
                            size = AppButtonSize.MINI,
                            style = AppButtonStyle.GRADIENT,
                        )
                    }
                    if (!showConfirm && !showPay && !showLogistics && !showRefund && !showComment) {
                        TextButton(onClick = onClick) {
                            Text(stringResource(Res.string.order_view_detail))
                        }
                    }
                }
            }
        }
    }
@Composable
private fun OrderStatus.tabLabel(): String = when (this) {
    OrderStatus.ALL -> stringResource(Res.string.order_status_all)
    OrderStatus.UNPAID -> stringResource(Res.string.order_status_unpaid)
    OrderStatus.UNSHIPPED -> stringResource(Res.string.order_status_unshipped)
    OrderStatus.UNRECEIVED -> stringResource(Res.string.order_status_unreceived)
    OrderStatus.AFTER_SALE -> stringResource(Res.string.order_status_after_sale)
    OrderStatus.UNEVALUATED -> stringResource(Res.string.order_status_unevaluated)
    OrderStatus.COMPLETED -> stringResource(Res.string.order_status_completed_tab)
}

