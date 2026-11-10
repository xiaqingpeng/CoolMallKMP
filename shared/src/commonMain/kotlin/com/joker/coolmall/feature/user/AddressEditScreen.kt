package com.joker.coolmall.feature.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.core.ui.resolveUiMessageSuspend
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.address_add
import myapplication.shared.generated.resources.address_city
import myapplication.shared.generated.resources.address_contact
import myapplication.shared.generated.resources.address_default_hint
import myapplication.shared.generated.resources.address_default_switch
import myapplication.shared.generated.resources.address_detail
import myapplication.shared.generated.resources.address_district
import myapplication.shared.generated.resources.address_edit
import myapplication.shared.generated.resources.address_phone
import myapplication.shared.generated.resources.address_province
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_save
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressEditRoute(
    addressId: Long = 0,
    onBack: () -> Unit,
    viewModel: AddressEditViewModel = viewModel(
        key = "address_edit_$addressId",
        factory = viewModelFactory {
            initializer {
                AddressEditViewModel(
                    addressRepository = AppContainerProvider.instance.addressRepository,
                    addressId = addressId,
                )
            }
        }
    ),
) {
    val form by viewModel.form.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            snackbarHostState.showSnackbar(resolveUiMessageSuspend(msg))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.saved.collect { onBack() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (form.isEditMode) {
                            stringResource(Res.string.address_edit)
                        } else {
                            stringResource(Res.string.address_add)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                AppButton(
                    text = stringResource(Res.string.common_save),
                    onClick = viewModel::submit,
                    enabled = form.canSubmit && !isSubmitting,
                    loading = isSubmitting,
                    size = AppButtonSize.MEDIUM,
                    style = AppButtonStyle.GRADIENT,
                    fullWidth = true,
                )
            }
        },
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AddressFormFields(form = form, viewModel = viewModel)
        }
    }
}

@Composable
private fun AddressFormFields(
    form: AddressForm,
    viewModel: AddressEditViewModel,
) {
    OutlinedTextField(
        value = form.contact,
        onValueChange = viewModel::updateContact,
        label = { Text(stringResource(Res.string.address_contact)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = form.phone,
        onValueChange = viewModel::updatePhone,
        label = { Text(stringResource(Res.string.address_phone)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = form.province,
            onValueChange = viewModel::updateProvince,
            label = { Text(stringResource(Res.string.address_province)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
            value = form.city,
            onValueChange = viewModel::updateCity,
            label = { Text(stringResource(Res.string.address_city)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
            value = form.district,
            onValueChange = viewModel::updateDistrict,
            label = { Text(stringResource(Res.string.address_district)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
    }
    OutlinedTextField(
        value = form.address,
        onValueChange = viewModel::updateAddress,
        label = { Text(stringResource(Res.string.address_detail)) },
        minLines = 2,
        modifier = Modifier.fillMaxWidth(),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.address_default_switch),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(Res.string.address_default_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = form.isDefault,
            onCheckedChange = viewModel::updateIsDefault,
        )
    }
    val error = form.validationError
    if (error != null) {
        Text(
            text = "$error",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}
