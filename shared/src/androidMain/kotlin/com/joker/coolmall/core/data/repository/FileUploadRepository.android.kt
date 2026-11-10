package com.joker.coolmall.core.data.repository

import android.content.Context
import android.net.Uri
import com.joker.coolmall.core.di.AppContainerProvider
import java.io.File

actual suspend fun readImageBytes(uri: String): ByteArray? {
    val context = AppContainerProvider.appContext as? Context ?: return null
    return try {
        when {
            uri.startsWith("content://") || uri.startsWith("file://") -> {
                context.contentResolver.openInputStream(Uri.parse(uri))?.use { it.readBytes() }
            }
            else -> {
                val file = File(uri)
                if (file.exists()) file.readBytes() else null
            }
        }
    } catch (_: Exception) {
        null
    }
}
