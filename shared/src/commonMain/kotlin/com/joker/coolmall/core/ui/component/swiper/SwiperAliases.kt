package com.joker.coolmall.core.ui.component.swiper

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.ui.component.Swiper as SharedSwiper

@Composable
fun <T> WeSwiper(
    options: List<T>,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState { options.size.coerceAtLeast(1) },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSpacing: Dp = 0.dp,
    autoplay: Boolean = true,
    interval: Long = 3000,
    indicator: (@Composable BoxScope.(Int, Int) -> Unit)? = null,
    content: @Composable PagerScope.(Int, T) -> Unit,
) = SharedSwiper(
    items = options,
    modifier = modifier,
    state = state,
    autoplay = autoplay,
    intervalMillis = interval,
    contentPadding = contentPadding,
    pageSpacing = pageSpacing,
    indicator = indicator,
    content = content,
)
