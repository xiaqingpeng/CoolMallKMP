package com.joker.coolmallkmp.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.ui.theme.PrimaryDefault
import kotlinx.coroutines.delay

@Composable
fun <T> Swiper(
    items: List<T>,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState { items.size.coerceAtLeast(1) },
    autoplay: Boolean = true,
    intervalMillis: Long = 3000,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSpacing: Dp = 0.dp,
    indicator: (@Composable BoxScope.(Int, Int) -> Unit)? = { count, current ->
        SwiperIndicator(
            count,
            current,
            Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-10).dp),
        )
    },
    content: @Composable PagerScope.(Int, T) -> Unit,
) {
    if (items.isEmpty()) return
    if (autoplay && items.size > 1) {
        LaunchedEffect(state.currentPage, items.size) {
            delay(intervalMillis)
            state.animateScrollToPage((state.currentPage + 1) % items.size)
        }
    }
    Box(modifier) {
        HorizontalPager(
            state = state,
            contentPadding = contentPadding,
            pageSpacing = pageSpacing,
        ) { index -> content(index, items[index]) }
        indicator?.invoke(this, items.size, state.currentPage)
    }
}

@Composable
fun SwiperIndicator(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .width((count * 16).dp)
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(PrimaryDefault.copy(alpha = .2f)),
    ) {
        val offsetX by animateDpAsState(
            targetValue = (current.coerceIn(0, count - 1) * 16).dp,
            label = "swiper_indicator_offset",
        )
        Box(
            Modifier
                .width(16.dp)
                .height(3.dp)
                .offset(x = offsetX)
                .clip(RoundedCornerShape(2.dp))
                .background(PrimaryDefault.copy(alpha = .7f)),
        )
    }
}
