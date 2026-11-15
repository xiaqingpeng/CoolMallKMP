package com.joker.coolmall.feature.main.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.joker.coolmall.feature.main.model.TopLevelDestination

@Composable
actual fun PlatformBottomNavIcon(
    destination: TopLevelDestination,
    selected: Boolean,
) {
    val icon = when (destination) {
        TopLevelDestination.HOME -> Icons.Filled.Home
        TopLevelDestination.CATEGORY -> Icons.Outlined.GridView
        TopLevelDestination.CART -> Icons.Filled.ShoppingCart
        TopLevelDestination.ME -> Icons.Filled.Person
    }
    Icon(imageVector = icon, contentDescription = null)
}
