package com.joker.coolmall.feature.main.component

import androidx.compose.runtime.Composable
import com.joker.coolmall.feature.main.model.TopLevelDestination

@Composable
expect fun PlatformBottomNavIcon(
    destination: TopLevelDestination,
    selected: Boolean,
)
