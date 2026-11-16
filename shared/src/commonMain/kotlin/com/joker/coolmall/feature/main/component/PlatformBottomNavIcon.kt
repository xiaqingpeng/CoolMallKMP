package com.joker.coolmallkmp.feature.main.component

import androidx.compose.runtime.Composable
import com.joker.coolmallkmp.feature.main.model.TopLevelDestination

@Composable
expect fun PlatformBottomNavIcon(
    destination: TopLevelDestination,
    selected: Boolean,
)
