package com.joker.coolmallkmp.core.util

import android.content.Context

actual object PackageUtils {
    actual fun packageName(context: Any?): String = (context as? Context)?.packageName.orEmpty()
    actual fun appName(context: Any?): String {
        val ctx = context as? Context ?: return ""
        return runCatching { ctx.getString(ctx.applicationInfo.labelRes) }.getOrDefault("")
    }
    actual fun versionName(context: Any?): String {
        val ctx = context as? Context ?: return ""
        return runCatching {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName.orEmpty()
        }.getOrDefault("")
    }
}
