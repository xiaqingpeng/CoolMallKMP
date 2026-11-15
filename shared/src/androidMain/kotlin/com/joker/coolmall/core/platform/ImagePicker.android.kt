package com.joker.coolmall.core.platform

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberPlatformImagePicker(
    maxCount: Int,
    onResult: (List<PickedImage>) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val limit = maxCount.coerceAtLeast(1)

    val singleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        onResult(
            uri?.let { listOf(it.toPickedImage(context.contentResolver)) }.orEmpty(),
        )
    }
    val multipleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = limit.coerceAtLeast(2),
        ),
    ) { uris ->
        onResult(uris.map { it.toPickedImage(context.contentResolver) })
    }

    return remember(singleLauncher, multipleLauncher, limit) {
        {
            val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            if (limit == 1) {
                singleLauncher.launch(request)
            } else {
                multipleLauncher.launch(request)
            }
        }
    }
}

private fun Uri.toPickedImage(resolver: ContentResolver): PickedImage {
    val mime = resolver.getType(this) ?: "image/jpeg"
    val name = lastPathSegment
        ?.substringAfterLast('/')
        ?.takeIf { it.isNotBlank() }
        ?: "image_${System.currentTimeMillis()}.jpg"
    val fileName = if (name.contains('.')) {
        name
    } else {
        val ext = when {
            mime.contains("png") -> "png"
            mime.contains("webp") -> "webp"
            mime.contains("gif") -> "gif"
            else -> "jpg"
        }
        "$name.$ext"
    }
    return PickedImage(
        uri = toString(),
        fileName = fileName,
        mimeType = mime,
    )
}
