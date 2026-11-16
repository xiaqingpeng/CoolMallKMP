package com.joker.coolmallkmp.core.platform

import androidx.compose.runtime.Composable

/**
 * 跨平台本地图片引用（路径或 content URI 字符串）。
 */
data class PickedImage(
    val uri: String,
    val fileName: String = "image.jpg",
    val mimeType: String = "image/jpeg",
)

/**
 * Compose 侧图片选择器：返回触发选图的 lambda。
 * Android：Photo Picker；iOS：PHPicker。取消返回空列表。
 */
@Composable
expect fun rememberPlatformImagePicker(
    maxCount: Int = 9,
    onResult: (List<PickedImage>) -> Unit,
): () -> Unit
