package com.joker.coolmallkmp.core.ui.component.image

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun NetWorkImage(
    model: String?,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    cornerShape: RoundedCornerShape = RoundedCornerShape(8.dp),
    contentScale: ContentScale = ContentScale.Crop,
    showBackground: Boolean = true,
) {
    AsyncImage(
        model = model,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .then(if (size != null) Modifier.size(size) else Modifier)
            .clip(cornerShape),
    )
}
