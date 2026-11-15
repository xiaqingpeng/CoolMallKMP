package com.joker.coolmall.feature.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.ui.component.SkeletonBlock
import com.joker.coolmall.core.ui.component.SkeletonCircle

@Composable
fun AddressListLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(3) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SkeletonCircle(36.dp)
                    SkeletonBlock(Modifier.width(92.dp).height(18.dp))
                    SkeletonBlock(Modifier.width(112.dp).height(16.dp))
                }
                SkeletonBlock(Modifier.fillMaxWidth(0.88f).height(16.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.66f).height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SkeletonBlock(Modifier.weight(1f).height(30.dp), RoundedCornerShape(6.dp))
                    SkeletonBlock(Modifier.weight(1f).height(30.dp), RoundedCornerShape(6.dp))
                }
            }
        }
    }
}
