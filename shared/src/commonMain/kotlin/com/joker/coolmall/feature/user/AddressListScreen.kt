package com.joker.coolmall.feature.user

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.empty.EmptyData
import com.joker.coolmall.core.ui.resolveUiMessageSuspend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListRoute(
    onBack: () -> Unit,
    onAddClick: () -> Unit = {},
    onEditClick: (Long) -> Unit = {},
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
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDelete by remember { mutableStateOf<Address?>(null) }

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.address_list_title),
                showBackIcon = true,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(Res.string.address_add),
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.addresses, key = { it.id }) { address ->
                            AddressCard(
                                address = address,
                                enabled = !isActing,
                                onEdit = { onEditClick(address.id) },
                                onDelete = { pendingDelete = address },
                                onSetDefault = { viewModel.setDefaultAddress(address) },
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
private fun AddressCard(
    address: Address,
    enabled: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onEdit),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = address.contact,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = address.phone,
                    style = MaterialTheme.typography.bodyMedium,
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = address.displayLine(),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = onSetDefault,
                    enabled = enabled && !address.isDefault,
                ) {
                    Text(
                        if (address.isDefault) {
                            stringResource(Res.string.address_is_default)
                        } else {
                            stringResource(Res.string.address_set_default)
                        }
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = onEdit, enabled = enabled) {
                    Text(stringResource(Res.string.common_edit))
                }
                IconButton(onClick = onDelete, enabled = enabled) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(Res.string.address_delete_title),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
