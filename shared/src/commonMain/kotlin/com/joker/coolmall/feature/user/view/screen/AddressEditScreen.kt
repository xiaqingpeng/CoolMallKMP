package com.joker.coolmallkmp.feature.user.view.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.ui.component.AppButton
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import com.joker.coolmallkmp.core.ui.component.AppScaffold
import com.joker.coolmallkmp.core.ui.resolveMessageCode
import com.joker.coolmallkmp.core.ui.resolveUiMessageSuspend
import com.joker.coolmallkmp.feature.user.component.RegionPickerModal
import com.joker.coolmallkmp.feature.user.data.RegionData
import com.joker.coolmallkmp.feature.user.viewmodel.AddressEditViewModel
import com.joker.coolmallkmp.feature.user.viewmodel.AddressForm
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.address_add
import coolmallkmp.shared.generated.resources.address_contact
import coolmallkmp.shared.generated.resources.address_contact_hint
import coolmallkmp.shared.generated.resources.address_default_hint
import coolmallkmp.shared.generated.resources.address_default_switch
import coolmallkmp.shared.generated.resources.address_detail
import coolmallkmp.shared.generated.resources.address_detail_hint
import coolmallkmp.shared.generated.resources.address_edit
import coolmallkmp.shared.generated.resources.address_phone
import coolmallkmp.shared.generated.resources.address_phone_hint
import coolmallkmp.shared.generated.resources.address_region
import coolmallkmp.shared.generated.resources.address_region_hint
import coolmallkmp.shared.generated.resources.common_save
import org.jetbrains.compose.resources.stringResource

@Suppress("ParamsComparedByRef")
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
    var showRegionPicker by remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            snackbarHostState.showSnackbar(resolveUiMessageSuspend(msg))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.saved.collect { onBack() }
    }

    AppScaffold(
        title = stringResource(
            if (form.isEditMode) Res.string.address_edit else Res.string.address_add,
        ),
        onBack = onBack,
        snackbarHostState = snackbarHostState,
        useLargeTopBar = true,
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .androidx.compose.foundation.layout.navigationBarsPadding(),
            ) {
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AddressFormFields(
                        form = form,
                        viewModel = viewModel,
                        onRegionClick = { showRegionPicker = true },
                        includeDefaultSwitch = false,
                    )
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                DefaultAddressRow(
                    checked = form.isDefault,
                    onCheckedChange = viewModel::updateIsDefault,
                )
            }
        }
    }
    RegionPickerModal(
        visible = showRegionPicker,
        regions = RegionData.getProvinces(),
        initialValue = "${form.province} ${form.city} ${form.district}",
        onDismiss = { showRegionPicker = false },
        onSelected = { province, city, district ->
            viewModel.updateProvince(province)
            viewModel.updateCity(city)
            viewModel.updateDistrict(district)
        },
    )
}

@Suppress("ParamsComparedByRef")
@Composable
private fun AddressFormFields(
    form: AddressForm,
    viewModel: AddressEditViewModel,
    onRegionClick: () -> Unit,
    includeDefaultSwitch: Boolean = true,
) {
    OutlinedTextField(
        value = form.contact,
        onValueChange = viewModel::updateContact,
        label = { Text(stringResource(Res.string.address_contact)) },
        placeholder = { Text(stringResource(Res.string.address_contact_hint)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    )
    OutlinedTextField(
        value = form.phone,
        onValueChange = viewModel::updatePhone,
        label = { Text(stringResource(Res.string.address_phone)) },
        placeholder = { Text(stringResource(Res.string.address_phone_hint)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    )
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = if (
                form.province.isNotBlank() &&
                form.city.isNotBlank() &&
                form.district.isNotBlank()
            ) {
                "${form.province} ${form.city} ${form.district}"
            } else {
                ""
            },
            onValueChange = {},
            label = { Text(stringResource(Res.string.address_region)) },
            placeholder = {
                Text(stringResource(Res.string.address_region_hint))
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            shape = RoundedCornerShape(12.dp),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRegionClick,
                ),
        )
    }
    OutlinedTextField(
        value = form.address,
        onValueChange = viewModel::updateAddress,
        label = { Text(stringResource(Res.string.address_detail)) },
        placeholder = { Text(stringResource(Res.string.address_detail_hint)) },
        minLines = 3,
        maxLines = 5,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    )
    if (includeDefaultSwitch) {
        DefaultAddressRow(
            checked = form.isDefault,
            onCheckedChange = viewModel::updateIsDefault,
        )
    }
    val error = form.validationError
    if (error != null) {
        Text(
            text = resolveMessageCode(error),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun DefaultAddressRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}
