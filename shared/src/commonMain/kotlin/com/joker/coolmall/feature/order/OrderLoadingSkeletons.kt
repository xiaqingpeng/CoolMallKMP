package com.joker.coolmall.feature.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.ui.component.SkeletonBlock
import com.joker.coolmall.core.ui.component.SkeletonCircle

@Composable
fun OrderListLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(3) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    SkeletonBlock(Modifier.fillMaxWidth(0.38f).height(16.dp))
                    SkeletonBlock(Modifier.fillMaxWidth(0.18f).height(16.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { SkeletonBlock(Modifier.size(72.dp)) }
                    Spacer(Modifier.weight(1f))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SkeletonBlock(Modifier.width(72.dp).height(16.dp))
                        SkeletonBlock(Modifier.width(52.dp).height(14.dp))
                    }
                }
                SkeletonBlock(Modifier.fillMaxWidth().height(1.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    SkeletonBlock(Modifier.width(74.dp).height(30.dp))
                    Spacer(Modifier.width(8.dp))
                    SkeletonBlock(Modifier.width(74.dp).height(30.dp))
                }
            }
        }
    }
}

@Composable
fun OrderDetailLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AddressLoadingBlock()
        repeat(2) { OrderGoodsLoadingBlock() }
        OrderInfoLoadingBlock(lines = 4)
        OrderInfoLoadingBlock(lines = 4)
    }
}

@Composable
fun OrderLogisticsLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AddressLoadingBlock()
        OrderInfoLoadingBlock(lines = 4)
        OrderInfoLoadingBlock(lines = 4, withTimeline = true)
    }
}

@Composable
fun OrderConfirmLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AddressLoadingBlock()
        repeat(3) { OrderGoodsLoadingBlock() }
        SkeletonBlock(Modifier.fillMaxWidth().height(84.dp))
    }
}

@Composable
private fun AddressLoadingBlock() {
    Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SkeletonCircle(34.dp)
            SkeletonBlock(Modifier.width(88.dp).height(17.dp))
            SkeletonBlock(Modifier.width(108.dp).height(15.dp))
        }
        SkeletonBlock(Modifier.fillMaxWidth(0.9f).height(16.dp))
        SkeletonBlock(Modifier.fillMaxWidth(0.64f).height(15.dp))
    }
}

@Composable
private fun OrderGoodsLoadingBlock() {
    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SkeletonBlock(Modifier.size(76.dp).aspectRatio(1f))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            SkeletonBlock(Modifier.fillMaxWidth(0.88f).height(17.dp))
            SkeletonBlock(Modifier.fillMaxWidth(0.62f).height(15.dp))
            SkeletonBlock(Modifier.fillMaxWidth(0.32f).height(16.dp))
        }
    }
}

@Composable
private fun OrderInfoLoadingBlock(lines: Int, withTimeline: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        SkeletonBlock(Modifier.fillMaxWidth(0.28f).height(18.dp))
        repeat(lines) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                if (withTimeline) SkeletonCircle(12.dp) else Spacer(Modifier.width(12.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.34f).height(15.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.28f).height(15.dp))
            }
        }
    }
}
