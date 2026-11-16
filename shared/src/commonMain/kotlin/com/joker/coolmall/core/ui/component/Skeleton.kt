package com.joker.coolmallkmp.core.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
private fun defaultSkeletonColor(): Color {
    val colorScheme = MaterialTheme.colorScheme
    return if (colorScheme.background.luminance() < 0.5f) {
        colorScheme.onSurface.copy(alpha = 0.14f)
    } else {
        colorScheme.onSurface.copy(alpha = 0.10f)
    }
}

@Composable
fun SkeletonBlock(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.42f,
        targetValue = 0.82f,
        animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse),
        label = "skeleton_alpha",
    )
    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .clip(shape)
            .background(defaultSkeletonColor()),
    )
}

@Composable
fun SkeletonCircle(
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    SkeletonBlock(
        modifier = modifier.size(size),
        shape = CircleShape,
    )
}

@Composable
fun GoodsGridSkeleton(
    modifier: Modifier = Modifier,
    count: Int = 6,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false,
    ) {
        items((0 until count).toList()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )
                SkeletonBlock(Modifier.fillMaxWidth(0.86f).height(16.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.62f).height(14.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.38f).height(18.dp))
            }
        }
    }
}

@Composable
fun PageSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonBlock(Modifier.size(64.dp), RoundedCornerShape(32.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SkeletonBlock(Modifier.fillMaxWidth(0.54f).height(18.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.82f).height(14.dp))
            }
        }
        SkeletonBlock(Modifier.fillMaxWidth().height(92.dp))
        repeat(4) {
            SkeletonBlock(Modifier.fillMaxWidth().height(56.dp))
        }
    }
}

@Composable
fun ListCardSkeleton(
    modifier: Modifier = Modifier,
    count: Int = 4,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(count) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SkeletonBlock(Modifier.size(58.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SkeletonBlock(Modifier.fillMaxWidth(0.42f).height(16.dp))
                        SkeletonBlock(Modifier.fillMaxWidth(0.78f).height(13.dp))
                    }
                }
                SkeletonBlock(Modifier.fillMaxWidth().height(14.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.62f).height(14.dp))
            }
        }
    }
}

@Composable
fun DetailSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SkeletonBlock(Modifier.fillMaxWidth().aspectRatio(1.05f))
        SkeletonBlock(Modifier.fillMaxWidth(0.84f).height(22.dp))
        SkeletonBlock(Modifier.fillMaxWidth(0.54f).height(18.dp))
        SkeletonBlock(Modifier.fillMaxWidth().height(82.dp))
        repeat(3) {
            SkeletonBlock(Modifier.fillMaxWidth().height(58.dp))
        }
    }
}
