package com.joker.coolmallkmp.feature.user.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.ui.component.AppDivider
import com.joker.coolmallkmp.core.ui.component.AppListItem
import com.joker.coolmallkmp.core.ui.component.Modal
import com.joker.coolmallkmp.core.ui.component.ModalType
import com.joker.coolmallkmp.feature.user.model.Region
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.address_region_choose
import coolmallkmp.shared.generated.resources.address_region_hint
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
        val parts = initialValue.trim().split(Regex("\\s+")).filter(String::isNotBlank)
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

    Modal(
        visible = true,
        modalType = ModalType.BOTTOM,
        onDismiss = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        indicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        horizontalPadding = 16.dp,
        skipPartiallyExpanded = false,
        dragHandleVerticalPadding = 16.dp,
        bottomContentPadding = 12.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.address_region_hint),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RegionLevelChip(
                    text = province?.name ?: stringResource(Res.string.address_region_choose),
                    selected = currentLevel == 0,
                    onClick = {
                        currentLevel = 0
                        city = null
                        district = null
                    },
                )
                if (province != null) {
                    RegionLevelChip(
                        text = city?.name ?: stringResource(Res.string.address_region_choose),
                        selected = currentLevel == 1,
                        onClick = {
                            currentLevel = 1
                            district = null
                        },
                    )
                }
                if (city != null) {
                    RegionLevelChip(
                    text = district?.name ?: stringResource(Res.string.address_region_choose),
                        selected = currentLevel == 2,
                        onClick = { currentLevel = 2 },
                    )
                }
            }
            AppDivider()
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(options) { item ->
                    val selected = when (currentLevel) {
                        0 -> item.id == province?.id
                        1 -> item.id == city?.id
                        2 -> item.id == district?.id
                        else -> false
                    }
                    AppListItem(
                        title = item.name,
                        showArrow = false,
                        verticalPadding = 16.dp,
                        trailingContent = if (selected) {
                            {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape,
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                            }
                        } else {
                            null
                        },
                        onClick = {
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
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RegionLevelChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        color = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .background(
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(50),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}
