package com.joker.coolmallkmp.feature.cs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.ui.component.SkeletonBlock
import com.joker.coolmallkmp.core.ui.component.SkeletonCircle

@Composable
fun ChatLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            SkeletonBlock(Modifier.align(Alignment.CenterHorizontally).width(88.dp).height(24.dp))
            ChatMessageSkeleton(outgoing = false, width = 92.dp)
            ChatMessageSkeleton(outgoing = true, width = 74.dp)
            ChatMessageSkeleton(outgoing = false, width = 112.dp)
            ChatMessageSkeleton(outgoing = true, width = 84.dp)
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SkeletonBlock(Modifier.weight(1f).height(44.dp), RoundedCornerShape(18.dp))
            Spacer(Modifier.width(8.dp))
            SkeletonBlock(Modifier.width(68.dp).height(36.dp), RoundedCornerShape(18.dp))
        }
    }
}

@Composable
private fun ChatMessageSkeleton(outgoing: Boolean, width: androidx.compose.ui.unit.Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (outgoing) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        if (!outgoing) SkeletonCircle(36.dp)
        if (!outgoing) Spacer(Modifier.width(8.dp))
        SkeletonBlock(Modifier.width(width).height(42.dp), RoundedCornerShape(16.dp))
        if (outgoing) Spacer(Modifier.width(8.dp))
        if (outgoing) SkeletonCircle(36.dp)
    }
}
