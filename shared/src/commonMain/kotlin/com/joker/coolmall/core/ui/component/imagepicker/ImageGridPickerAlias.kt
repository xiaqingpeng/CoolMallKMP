package com.joker.coolmallkmp.core.ui.component.imagepicker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joker.coolmallkmp.core.platform.PickedImage
import com.joker.coolmallkmp.core.ui.component.ImageGridPicker as SharedImageGridPicker

@Composable
fun ImageGridPicker(
    selectedImages: List<PickedImage>,
    onImagesChanged: (List<PickedImage>) -> Unit,
    maxImages: Int = 9,
    modifier: Modifier = Modifier,
) = SharedImageGridPicker(selectedImages, onImagesChanged, maxImages, modifier)
