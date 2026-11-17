package com.joker.coolmallkmp.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import com.joker.coolmallkmp.core.model.entity.Address
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.address_is_default
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddressCard(
    address: Address?,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    showBottomBar: Boolean = true,
    actionContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    ) {
        if (address == null) {
            ListItem(
                title = "请选择收货地址",
                description = "点击添加或选择收货地址",
                showDivider = false,
                onClick = onClick,
            )
            return@Card
        }
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = address.fullRegion(),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = address.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                actionContent?.invoke()
                if (selected) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
            if (showBottomBar) {
                AppDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        address.contact,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(address.phone, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                    if (address.isDefault) {
                        Text(
                            text = stringResource(Res.string.address_is_default),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
