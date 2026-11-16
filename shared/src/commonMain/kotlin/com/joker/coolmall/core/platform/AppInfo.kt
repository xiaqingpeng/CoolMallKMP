package com.joker.coolmallkmp.core.platform

/**
 * 平台应用版本信息。
 * Android 通过 PackageManager；iOS 通过 NSBundle。
 */
expect fun appVersionName(context: Any?): String

expect fun appVersionCode(context: Any?): String
