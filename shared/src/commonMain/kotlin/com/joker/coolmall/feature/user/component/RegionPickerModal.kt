package com.joker.coolmall.feature.user.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.BottomSheetDefaults
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
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
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
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                Text(
                    text = "${stringResource(Res.string.address_province)} / ${stringResource(Res.string.address_city)} / ${stringResource(Res.string.address_district)}",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = currentLevel == 0,
                        onClick = {
                            currentLevel = 0
                        },
                        label = { Text(province?.name ?: stringResource(Res.string.address_province)) },
                    )
                    if (province != null) {
                        FilterChip(
                            selected = currentLevel == 1,
                            onClick = {
                                currentLevel = 1
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
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
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
                                            onSelected(
                                                province?.name.orEmpty(),
                                                city?.name.orEmpty(),
                                                item.name,
                                            )
                                            onDismiss()
                                        }
                                    }
                                }
                                .padding(12.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
}
