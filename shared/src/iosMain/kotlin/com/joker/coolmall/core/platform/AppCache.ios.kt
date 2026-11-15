package com.joker.coolmall.core.platform

import platform.Foundation.NSFileManager
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSHomeDirectory

@OptIn(ExperimentalForeignApi::class)
actual fun clearAppCache(): Boolean {
    return runCatching {
        val cachePath = "${NSHomeDirectory()}/Library/Caches"
        val fileManager = NSFileManager.defaultManager
        fileManager.contentsOfDirectoryAtPath(cachePath, error = null)
            ?.forEach { name ->
                fileManager.removeItemAtPath("$cachePath/$name", error = null)
            }
        true
    }.getOrDefault(false)
}
