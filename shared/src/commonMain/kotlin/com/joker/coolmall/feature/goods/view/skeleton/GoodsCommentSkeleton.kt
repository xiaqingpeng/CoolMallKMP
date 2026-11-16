package com.joker.coolmallkmp.feature.goods.view.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.ui.component.SkeletonBlock
import com.joker.coolmallkmp.core.ui.component.SkeletonCircle

@Composable
fun GoodsCommentLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(2) { withImages ->
            Row(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                SkeletonCircle(40.dp)
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SkeletonBlock(Modifier.weight(1f).size(96.dp, 16.dp))
                        SkeletonBlock(Modifier.weight(1f).size(76.dp, 16.dp))
                    }
                    SkeletonBlock(Modifier.fillMaxWidth(0.3f).size(60.dp, 14.dp))
                    SkeletonBlock(Modifier.fillMaxWidth().size(180.dp, 14.dp))
                    SkeletonBlock(Modifier.fillMaxWidth(0.9f).size(160.dp, 14.dp))
                    if (withImages == 0) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(3) {
                                SkeletonBlock(Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
