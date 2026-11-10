package com.joker.coolmall.core.platform

import android.content.Context

actual fun appVersionName(context: Any?): String {
    val ctx = context as? Context ?: return ""
    return runCatching {
        ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: ""
    }.getOrDefault("")
}

actual fun appVersionCode(context: Any?): String {
    val ctx = context as? Context ?: return ""
    return runCatching {
        ctx.packageManager.getPackageInfo(ctx.packageName, 0).longVersionCode.toString()
    }.getOrDefault("")
}
