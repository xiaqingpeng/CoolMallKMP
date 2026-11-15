package com.joker.coolmall.feature.order

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import com.joker.coolmall.core.model.entity.Logistics
import com.joker.coolmall.core.model.entity.LogisticsItem
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_retry
import myapplication.shared.generated.resources.order_logistics_carrier_unknown
import myapplication.shared.generated.resources.order_logistics_courier
import myapplication.shared.generated.resources.order_logistics_empty
import myapplication.shared.generated.resources.order_logistics_in_transit
import myapplication.shared.generated.resources.order_logistics_number
import myapplication.shared.generated.resources.order_logistics_number_empty
import myapplication.shared.generated.resources.order_logistics_take_time
import myapplication.shared.generated.resources.order_logistics_title
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.empty.EmptyUiMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderLogisticsRoute(
    orderId: Long,
    onBack: () -> Unit,
    viewModel: OrderLogisticsViewModel = viewModel(
        key = "order_logistics_$orderId",
        factory = viewModelFactory {
            initializer {
                OrderLogisticsViewModel(
                    orderId = orderId,
                    orderRepository = AppContainerProvider.instance.orderRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.order_logistics_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        }
    ) { padding ->
        when (val state = uiState) {
            OrderLogisticsUiState.Loading -> {
                OrderLogisticsLoadingSkeleton(Modifier.padding(padding))
            }

            is OrderLogisticsUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::load,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }

            is OrderLogisticsUiState.Success -> {
                LogisticsContent(
                    logistics = state.logistics ?: Logistics(),
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun LogisticsContent(
    logistics: Logistics,
    modifier: Modifier = Modifier,
) {
    val tracks = logistics.list.orEmpty()
    val numberEmpty = stringResource(Res.string.order_logistics_number_empty)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = logistics.expName?.takeIf { it.isNotBlank() }
                            ?: stringResource(Res.string.order_logistics_carrier_unknown),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            Res.string.order_logistics_number,
                            logistics.number?.takeIf { it.isNotBlank() } ?: numberEmpty,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (!logistics.courier.isNullOrBlank()) {
                        val phone = logistics.courierPhone.orEmpty()
                        val courierText = logistics.courier +
                            if (phone.isNotBlank()) " $phone" else ""
                        Text(
                            text = stringResource(Res.string.order_logistics_courier, courierText),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (!logistics.takeTime.isNullOrBlank()) {
                        Text(
                            text = stringResource(
                                Res.string.order_logistics_take_time,
                                logistics.takeTime.orEmpty(),
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        if (tracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.order_logistics_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            itemsIndexed(tracks) { index, item ->
                LogisticsTrackItem(
                    item = item,
                    isFirst = index == 0,
                    isLast = index == tracks.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun LogisticsTrackItem(
    item: LogisticsItem,
    isFirst: Boolean,
    isLast: Boolean,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(if (isFirst) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFirst) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        }
                    ),
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(48.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.status.orEmpty().ifBlank {
                    stringResource(Res.string.order_logistics_in_transit)
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isFirst) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isFirst) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            if (!item.time.isNullOrBlank()) {
                Text(
                    text = item.time.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
