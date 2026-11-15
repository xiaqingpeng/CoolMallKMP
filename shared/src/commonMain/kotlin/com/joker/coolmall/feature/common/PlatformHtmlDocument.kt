package com.joker.coolmall.feature.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformHtmlDocument(
    html: String,
    modifier: Modifier = Modifier,
)
