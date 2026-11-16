package com.joker.coolmallkmp.core.ui.component.address

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joker.coolmallkmp.core.model.entity.Address
import com.joker.coolmallkmp.core.ui.component.AddressCard as SharedAddressCard

@Composable
fun AddressCard(
    modifier: Modifier = Modifier,
    address: Address? = null,
    onClick: () -> Unit = {},
    actionSlot: (@Composable () -> Unit)? = null,
    showBottomBar: Boolean = true,
    addressSelected: Boolean = false,
) = SharedAddressCard(
    address = address,
    modifier = modifier,
    selected = addressSelected,
    showBottomBar = showBottomBar,
    actionContent = actionSlot,
    onClick = onClick,
)
