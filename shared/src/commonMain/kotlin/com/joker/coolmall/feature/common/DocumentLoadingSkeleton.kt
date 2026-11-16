package com.joker.coolmallkmp.feature.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.ui.component.SkeletonBlock

@Composable
internal fun DocumentLoadingSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SkeletonBlock(Modifier.fillMaxWidth(0.36f).height(36.dp))
        SkeletonBlock(Modifier.fillMaxWidth(0.44f).height(18.dp))
        DocumentParagraph(listOf(0.96f, 0.92f, 0.98f, 0.90f))
        DocumentParagraph(listOf(0.94f, 0.88f, 0.96f))
        SkeletonBlock(Modifier.fillMaxWidth(0.52f).height(30.dp))
        SkeletonBlock(Modifier.fillMaxWidth(0.40f).height(24.dp))
        DocumentParagraph(listOf(0.97f, 0.95f, 0.93f, 0.90f, 0.86f))
    }
}

@Composable
private fun DocumentParagraph(lines: List<Float>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        lines.forEach { fraction ->
            SkeletonBlock(Modifier.fillMaxWidth(fraction).height(18.dp))
        }
    }
}
