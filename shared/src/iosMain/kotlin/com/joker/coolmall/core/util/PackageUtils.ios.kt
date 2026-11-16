package com.joker.coolmall.core.util

import platform.Foundation.NSBundle

actual object PackageUtils {
    actual fun packageName(context: Any?): String =
        NSBundle.mainBundle.bundleIdentifier.orEmpty()
    actual fun appName(context: Any?): String =
        (NSBundle.mainBundle.infoDictionary?.get("CFBundleDisplayName") as? String)
            ?: (NSBundle.mainBundle.infoDictionary?.get("CFBundleName") as? String)
            ?: ""
    actual fun versionName(context: Any?): String =
        NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: ""
}
