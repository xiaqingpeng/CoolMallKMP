package com.joker.coolmall.core.ui.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 间距 token（对齐 CoolMallKotlin Size.kt 常用档）
 */
val SpaceVerticalXSmall = 4.dp
val SpaceVerticalSmall = 8.dp
val SpaceVerticalMedium = 12.dp
val SpaceVerticalLarge = 16.dp

val SpaceHorizontalXSmall = 4.dp
val SpaceHorizontalSmall = 8.dp
val SpaceHorizontalMedium = 12.dp
val SpaceHorizontalLarge = 16.dp

val SpacePaddingXSmall = 4.dp
val SpacePaddingSmall = 8.dp
val SpacePaddingMedium = 12.dp
val SpacePaddingLarge = 16.dp

@Composable
fun SpaceVerticalXSmall() {
    Spacer(modifier = Modifier.height(SpaceVerticalXSmall))
}

@Composable
fun SpaceVerticalSmall() {
    Spacer(modifier = Modifier.height(SpaceVerticalSmall))
}

@Composable
fun SpaceVerticalMedium() {
    Spacer(modifier = Modifier.height(SpaceVerticalMedium))
}

@Composable
fun SpaceVerticalLarge() {
    Spacer(modifier = Modifier.height(SpaceVerticalLarge))
}

@Composable
fun SpaceHorizontalSmall() {
    Spacer(modifier = Modifier.width(SpaceHorizontalSmall))
}

@Composable
fun SpaceHorizontalMedium() {
    Spacer(modifier = Modifier.width(SpaceHorizontalMedium))
}
