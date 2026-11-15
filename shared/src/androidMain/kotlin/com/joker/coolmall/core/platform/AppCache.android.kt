package com.joker.coolmall.core.platform

import android.content.Context
import com.joker.coolmall.core.di.AppContainerProvider

actual fun clearAppCache(): Boolean {
    val context = AppContainerProvider.appContext as? Context ?: return false
    return runCatching {
        context.cacheDir.listFiles()?.forEach { it.deleteRecursively() }
        true
    }.getOrDefault(false)
}
