package com.joker.coolmallkmp.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.joker.coolmallkmp.core.platform.PickedImage
import com.joker.coolmallkmp.core.platform.rememberPlatformImagePicker

@Composable
fun ImageGridPicker(
    selectedImages: List<PickedImage>,
    onImagesChanged: (List<PickedImage>) -> Unit,
    maxImages: Int = 9,
    modifier: Modifier = Modifier,
) {
    val launchPicker = rememberPlatformImagePicker(maxImages) { picked ->
        onImagesChanged((selectedImages + picked).distinctBy { it.uri }.take(maxImages))
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false,
    ) {
        items(selectedImages, key = { it.uri }) { image ->
            Box(
                modifier = Modifier.aspectRatio(1f).background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                AsyncImage(
                    model = image.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
                IconButton(
                    onClick = { onImagesChanged(selectedImages.filterNot { it.uri == image.uri }) },
                    modifier = Modifier.align(Alignment.TopEnd).size(28.dp),
                ) {
                    Icon(Icons.Default.Close, contentDescription = "删除")
                }
            }
        }
        if (selectedImages.size < maxImages) {
            item {
                Box(
                    modifier = Modifier.aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = launchPicker) {
                        Icon(Icons.Default.Add, contentDescription = "添加图片")
                    }
                }
            }
        }
    }
}
