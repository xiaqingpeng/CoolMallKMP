package com.joker.coolmall.feature.main.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.ui.component.SkeletonBlock

@Composable
internal fun CategoryLoadingSkeleton() {
    Row(Modifier.fillMaxSize()) {
        Column(
            Modifier.width(100.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            repeat(8) {
                Row(Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 12.dp)) {
                    SkeletonBlock(Modifier.fillMaxWidth(0.56f).height(14.dp))
                }
            }
        }
        Column(
            Modifier.weight(1f).fillMaxHeight().padding(horizontal = 8.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            repeat(4) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SkeletonBlock(Modifier.width(3.dp).height(18.dp))
                        SkeletonBlock(Modifier.fillMaxWidth(0.3f).height(18.dp))
                    }
                    repeat(2) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    SkeletonBlock(Modifier.fillMaxWidth().height(92.dp))
                                    SkeletonBlock(Modifier.fillMaxWidth(0.72f).height(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
