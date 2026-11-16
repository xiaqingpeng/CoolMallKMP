package com.joker.coolmall.core.ui.component.image

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Avatar(
    model: String?,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) = NetWorkImage(
    model = model,
    modifier = modifier,
    size = size,
    cornerShape = CircleShape,
)
