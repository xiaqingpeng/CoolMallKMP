package com.joker.coolmall.feature.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.model.entity.Address
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.address_add
import myapplication.shared.generated.resources.address_delete_confirm
import myapplication.shared.generated.resources.address_delete_title
import myapplication.shared.generated.resources.address_empty
import myapplication.shared.generated.resources.address_is_default
import myapplication.shared.generated.resources.address_list_title
import myapplication.shared.generated.resources.address_set_default
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_cancel
import myapplication.shared.generated.resources.common_default
import myapplication.shared.generated.resources.common_delete
import myapplication.shared.generated.resources.common_edit
import myapplication.shared.generated.resources.common_retry
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.core.ui.component.LoadMore
import com.joker.coolmall.core.ui.component.RefreshLayout
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.empty.EmptyData
import com.joker.coolmall.core.ui.resolveUiMessageSuspend
import androidx.compose.ui.graphics.vector.ImageVector
import com.joker.coolmall.core.ui.component.AddressCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListRoute(
    onBack: () -> Unit,
    onAddClick: () -> Unit = {},
    onEditClick: (Long) -> Unit = {},
    isSelectMode: Boolean = false,
    onAddressSelected: (Address) -> Unit = {},
    viewModel: AddressListViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AddressListViewModel(AppContainerProvider.instance.addressRepository)
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isActing by viewModel.isActing.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDelete by remember { mutableStateOf<Address?>(null) }
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && last >= total - 3
        }
    }

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }
    LaunchedEffect(shouldLoadMore, uiState, isRefreshing, isLoadingMore) {
        val hasMore = (uiState as? AddressListUiState.Success)?.hasMore == true
        if (shouldLoadMore && hasMore && !isRefreshing && !isLoadingMore) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.address_list_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState !is AddressListUiState.Loading && uiState !is AddressListUiState.Error) {
                AppButton(
                    text = stringResource(Res.string.address_add),
                    onClick = onAddClick,
                    size = AppButtonSize.MEDIUM,
                    style = AppButtonStyle.GRADIENT,
                    fullWidth = true,
                    modifier = Modifier.padding(16.dp),
                )
            }
        },
    ) { padding ->
        when (val state = uiState) {
            AddressListUiState.Loading -> {
                AddressListLoadingSkeleton(Modifier.padding(padding))
            }

            is AddressListUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::load,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }

            is AddressListUiState.Success -> {
                if (state.addresses.isEmpty()) {
                    EmptyData(
                        message = stringResource(Res.string.address_empty),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    )
                } else {
                    RefreshLayout(
                        isRefreshing = isRefreshing,
                        onRefresh = viewModel::refresh,
                        loadMoreState = viewModel.loadMoreState.collectAsState().value,
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    ) {
                        items(state.addresses, key = { it.id }) { address ->
                            AddressCard(
                                address = address,
                                onClick = {
                                    if (isSelectMode) onAddressSelected(address)
                                    else onEditClick(address.id)
                                },
                                actionContent = {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        AddressActionButton(
                                            enabled = !isActing,
                                            onClick = {
                                                if (isSelectMode) onAddressSelected(address)
                                                else onEditClick(address.id)
                                            },
                                            icon = Icons.Filled.Edit,
                                            contentDescription = stringResource(Res.string.common_edit),
                                        )
                                        if (!isSelectMode) {
                                            AddressActionButton(
                                                enabled = !isActing,
                                                onClick = { pendingDelete = address },
                                                icon = Icons.Filled.Delete,
                                                contentDescription = stringResource(Res.string.address_delete_title),
                                            )
                                        }
                                    }
                                },
                            )
                        }
                        item {
                            LoadMore(
                                state = viewModel.loadMoreState.collectAsState().value,
                                onRetry = viewModel::loadMore,
                            )
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { address ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(Res.string.address_delete_title)) },
            text = {
                Text(stringResource(Res.string.address_delete_confirm, address.contact))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAddress(address.id)
                        pendingDelete = null
                    }
                ) {
                    Text(stringResource(Res.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            },
        )
    }
}

@Composable
private fun AddressActionButton(
    enabled: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}
