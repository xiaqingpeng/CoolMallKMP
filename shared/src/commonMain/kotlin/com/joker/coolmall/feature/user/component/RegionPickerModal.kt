package com.joker.coolmall.feature.user.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmall.feature.user.model.Region
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.address_city
import myapplication.shared.generated.resources.address_district
import myapplication.shared.generated.resources.address_province
import myapplication.shared.generated.resources.common_cancel
import myapplication.shared.generated.resources.common_confirm
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegionPickerModal(
    visible: Boolean,
    regions: List<Region>,
    initialValue: String,
    onDismiss: () -> Unit,
    onSelected: (province: String, city: String, district: String) -> Unit,
) {
    if (!visible) return
    var province by remember(initialValue) { mutableStateOf<Region?>(null) }
    var city by remember(initialValue) { mutableStateOf<Region?>(null) }
    var district by remember(initialValue) { mutableStateOf<Region?>(null) }
    var currentLevel by remember(initialValue) { mutableStateOf(0) }
    LaunchedEffect(initialValue, regions) {
        val parts = initialValue.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val selectedProvince = regions.firstOrNull { it.name == parts.getOrNull(0) }
        val selectedCity = selectedProvince?.children?.firstOrNull { it.name == parts.getOrNull(1) }
        val selectedDistrict = selectedCity?.children?.firstOrNull { it.name == parts.getOrNull(2) }
        province = selectedProvince
        city = selectedCity
        district = selectedDistrict
        currentLevel = when {
            selectedDistrict != null -> 2
            selectedCity != null -> 1
            else -> 0
        }
    }
    val options = when (currentLevel) {
        0 -> regions
        1 -> province?.children.orEmpty()
        else -> city?.children.orEmpty()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${stringResource(Res.string.address_province)} / ${stringResource(Res.string.address_city)} / ${stringResource(Res.string.address_district)}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = currentLevel == 0,
                        onClick = {
                            currentLevel = 0
                            city = null
                            district = null
                        },
                        label = { Text(province?.name ?: stringResource(Res.string.address_province)) },
                    )
                    if (province != null) {
                        FilterChip(
                            selected = currentLevel == 1,
                            onClick = {
                                currentLevel = 1
                                district = null
                            },
                            label = { Text(city?.name ?: stringResource(Res.string.address_city)) },
                        )
                    }
                    if (city != null) {
                        FilterChip(
                            selected = currentLevel == 2,
                            onClick = { currentLevel = 2 },
                            label = { Text(district?.name ?: stringResource(Res.string.address_district)) },
                        )
                    }
                }
                LazyColumn {
                    items(options) { item ->
                        Text(
                            text = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (currentLevel) {
                                        0 -> {
                                            province = item
                                            city = null
                                            district = null
                                            currentLevel = if (item.children.isEmpty()) 0 else 1
                                        }
                                        1 -> {
                                            city = item
                                            district = null
                                            currentLevel = if (item.children.isEmpty()) 1 else 2
                                        }
                                        else -> {
                                            district = item
                                        }
                                    }
                                }
                                .padding(12.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = province != null && city != null && district != null,
                onClick = {
                    val selectedProvince = province ?: return@TextButton
                    val selectedCity = city ?: return@TextButton
                    val selectedDistrict = district ?: return@TextButton
                    onSelected(selectedProvince.name, selectedCity.name, selectedDistrict.name)
                    onDismiss()
                },
            ) {
                Text(stringResource(Res.string.common_confirm))
            }
        },
        dismissButton = { FilterChip(selected = false, onClick = onDismiss, label = { Text(stringResource(Res.string.common_cancel)) }) },
    )
}
