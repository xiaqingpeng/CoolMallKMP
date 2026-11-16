package com.joker.coolmallkmp.feature.user.view.screen
import com.joker.coolmallkmp.feature.user.viewmodel.AddressListViewModel
import com.joker.coolmallkmp.feature.user.viewmodel.*
import com.joker.coolmallkmp.feature.user.view.skeleton.AddressListLoadingSkeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.model.entity.Address
import com.joker.coolmallkmp.core.ui.component.AddressCard
import com.joker.coolmallkmp.core.ui.component.AppButton
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import com.joker.coolmallkmp.core.ui.component.LoadMore
import com.joker.coolmallkmp.core.ui.component.RefreshLayout
import com.joker.coolmallkmp.core.ui.empty.EmptyData
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import com.joker.coolmallkmp.core.ui.resolveUiMessageSuspend
import com.joker.coolmallkmp.feature.main.component.CenterTopAppBar
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.address_add
import coolmallkmp.shared.generated.resources.address_delete_confirm
import coolmallkmp.shared.generated.resources.address_delete_title
import coolmallkmp.shared.generated.resources.address_empty
import coolmallkmp.shared.generated.resources.address_list_title
import coolmallkmp.shared.generated.resources.common_cancel
import coolmallkmp.shared.generated.resources.common_delete
import coolmallkmp.shared.generated.resources.common_edit
import org.jetbrains.compose.resources.stringResource

@Suppress("ParamsComparedByRef")
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
                                            onClick = { onEditClick(address.id) },
                                            icon = Icons.Filled.Edit,
                                            contentDescription = stringResource(Res.string.common_edit),
                                        )
                                        AddressActionButton(
                                            enabled = !isActing,
                                            onClick = { pendingDelete = address },
                                            icon = Icons.Filled.Delete,
                                            contentDescription = stringResource(Res.string.address_delete_title),
                                        )
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
