package com.joker.coolmallkmp.feature.main.view.skeleton

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.ui.component.SkeletonBlock

@Composable
internal fun HomeLoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SkeletonBlock(
            Modifier
                .fillMaxWidth()
                .aspectRatio(2f),
        )
        Card {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SkeletonBlock(Modifier.size(28.dp), RoundedCornerShape(4.dp))
                SkeletonBlock(Modifier.weight(1f).height(16.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.18f).height(16.dp))
            }
        }
        Card {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        repeat(5) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                SkeletonBlock(Modifier.size(50.dp), RoundedCornerShape(25.dp))
                                Spacer(Modifier.height(6.dp))
                                SkeletonBlock(Modifier.fillMaxWidth(0.72f).height(12.dp))
                            }
                        }
                    }
                }
            }
        }
        Card {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBlock(Modifier.size(18.dp), RoundedCornerShape(4.dp))
                    SkeletonBlock(Modifier.weight(1f).height(16.dp))
                    SkeletonBlock(Modifier.fillMaxWidth(0.18f).height(14.dp))
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items((0 until 4).toList()) {
                        Column(Modifier.fillParentMaxWidth(0.3f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SkeletonBlock(Modifier.fillMaxWidth().aspectRatio(1f))
                            SkeletonBlock(Modifier.fillMaxWidth(0.84f).height(14.dp))
                            SkeletonBlock(Modifier.fillMaxWidth(0.52f).height(14.dp))
                        }
                    }
                }
            }
        }
        SkeletonBlock(Modifier.fillMaxWidth(0.32f).height(18.dp))
        repeat(3) {
            Card {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SkeletonBlock(Modifier.size(108.dp), RoundedCornerShape(8.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SkeletonBlock(Modifier.fillMaxWidth(0.72f).height(16.dp))
                        SkeletonBlock(Modifier.fillMaxWidth().height(14.dp))
                        SkeletonBlock(Modifier.fillMaxWidth(0.88f).height(14.dp))
                        Spacer(Modifier.height(8.dp))
                        SkeletonBlock(Modifier.fillMaxWidth(0.32f).height(18.dp))
                    }
                }
            }
        }
    }
}
