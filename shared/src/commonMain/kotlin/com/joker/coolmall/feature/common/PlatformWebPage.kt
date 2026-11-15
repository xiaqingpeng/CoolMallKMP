package com.joker.coolmall.feature.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformWebPage(
    url: String,
    reloadToken: Int = 0,
    onTitleChange: (String) -> Unit = {},
    onProgressChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
)
