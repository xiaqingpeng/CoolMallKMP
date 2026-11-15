package com.joker.coolmall.feature.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.ui.component.SkeletonBlock

@Composable
fun CouponListLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(3) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    SkeletonBlock(Modifier.size(46.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SkeletonBlock(Modifier.fillMaxWidth(0.82f).height(16.dp))
                        SkeletonBlock(Modifier.fillMaxWidth(0.48f).height(14.dp))
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SkeletonBlock(Modifier.width(52.dp).height(16.dp))
                        SkeletonBlock(Modifier.width(72.dp).height(14.dp))
                    }
                }
                SkeletonBlock(Modifier.fillMaxWidth().height(1.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    SkeletonBlock(Modifier.fillMaxWidth(0.42f).height(14.dp))
                    SkeletonBlock(Modifier.width(92.dp).height(32.dp))
                }
            }
        }
    }
}
