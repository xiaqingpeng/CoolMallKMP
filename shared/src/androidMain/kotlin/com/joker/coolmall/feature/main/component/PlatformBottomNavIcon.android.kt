package com.joker.coolmall.feature.main.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.joker.coolmall.feature.main.model.TopLevelDestination
import com.joker.coolmall.shared.R

@Composable
actual fun PlatformBottomNavIcon(
    destination: TopLevelDestination,
    selected: Boolean,
) {
    val resource = when (destination) {
        TopLevelDestination.HOME -> R.raw.home
        TopLevelDestination.CATEGORY -> R.raw.category
        TopLevelDestination.CART -> R.raw.cart
        TopLevelDestination.ME -> R.raw.me
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resource))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = selected,
    )
    LottieAnimation(
        composition = composition,
        progress = { if (selected) progress else 0f },
        modifier = Modifier.size(30.dp),
    )
}
