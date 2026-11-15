package com.joker.coolmall.feature.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import coil3.compose.AsyncImage
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import com.joker.coolmall.core.model.entity.Address
import com.joker.coolmall.core.model.entity.Cart
import com.joker.coolmall.core.model.entity.ConfirmOrder
import com.joker.coolmall.core.model.entity.Order
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.cart_default_spec
import myapplication.shared.generated.resources.cart_total_count
import myapplication.shared.generated.resources.cart_total_prefix
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_default
import myapplication.shared.generated.resources.common_retry
import myapplication.shared.generated.resources.order_confirm_add_address
import myapplication.shared.generated.resources.order_confirm_ios_pay_hint
import myapplication.shared.generated.resources.order_confirm_remark
import myapplication.shared.generated.resources.order_confirm_submit
import myapplication.shared.generated.resources.order_confirm_title
import myapplication.shared.generated.resources.price_times_count
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.component.AppButtonFixed
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.core.ui.component.PriceText
import com.joker.coolmall.core.ui.component.PriceTextSmall
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.resolveUiMessageSuspend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmRoute(
    onBack: () -> Unit,
    onSelectAddress: () -> Unit = {},
    onOrderCreated: (Order) -> Unit,
    viewModel: OrderConfirmViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val container = AppContainerProvider.instance
                OrderConfirmViewModel(
                    pageRepository = container.pageRepository,
                    orderRepository = container.orderRepository,
                    cartRepository = container.cartRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val remark by viewModel.remark.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedAddressResult by
        com.joker.coolmall.feature.user.navigation.AddressSelectionStore.selectedAddress
            .collectAsState()

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }
    LaunchedEffect(Unit) {
        viewModel.createdOrder.collect { onOrderCreated(it) }
    }
    LaunchedEffect(selectedAddressResult) {
        selectedAddressResult?.let {
            viewModel.selectAddress(it)
            com.joker.coolmall.feature.user.navigation.AddressSelectionStore.consume()
        }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.order_confirm_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.cart_total_count, viewModel.totalCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = stringResource(Res.string.cart_total_prefix),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        PriceText(
                            price = viewModel.totalAmount,
                            showDecimals = false,
                            integerStyle = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                AppButtonFixed(
                    text = stringResource(Res.string.order_confirm_submit),
                    onClick = viewModel::submitOrder,
                    enabled = !isSubmitting && !viewModel.isDraftEmpty,
                    loading = isSubmitting,
                    size = AppButtonSize.SMALL,
                    style = AppButtonStyle.GRADIENT,
                )
            }
        }
    ) { padding ->
        when (val state = uiState) {
            OrderConfirmUiState.Loading -> {
                OrderConfirmLoadingSkeleton(Modifier.padding(padding))
            }
            is OrderConfirmUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::loadConfirmPage,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            is OrderConfirmUiState.Success -> {
                OrderConfirmContent(
                    page = state.page,
                    address = selectedAddress,
                    onAddressClick = onSelectAddress,
                    carts = viewModel.carts,
                    remark = remark,
                    onRemarkChange = viewModel::updateRemark,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun OrderConfirmContent(
    page: ConfirmOrder,
    address: Address?,
    onAddressClick: () -> Unit,
    carts: List<Cart>,
    remark: String,
    onRemarkChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            AddressCard(
                address = address ?: page.defaultAddress,
                onClick = onAddressClick,
            )
        }
        items(carts, key = { "${it.goodsId}_${it.spec.hashCode()}" }) { cart ->
            CartSummaryCard(cart = cart)
        }
        item {
            OutlinedTextField(
                value = remark,
                onValueChange = onRemarkChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.order_confirm_remark)) },
                minLines = 2,
            )
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
private fun AddressCard(
    address: Address?,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
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
                        text = stringResource(Res.string.order_confirm_add_address),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
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
                        if (address.isDefault) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(Res.string.common_default),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
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
private fun CartSummaryCard(cart: Cart) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = cart.goodsMainPic,
                    contentDescription = cart.goodsName,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = cart.goodsName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                PriceTextSmall(price = cart.totalAmount)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            cart.spec.forEach { spec ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = spec.name.ifBlank { stringResource(Res.string.cart_default_spec) },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    PriceTextSmall(price = spec.price)
                    Text(
                        text = stringResource(Res.string.price_times_count, spec.count),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
