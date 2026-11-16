package com.joker.coolmallkmp.core.util

expect object PackageUtils {
    fun packageName(context: Any?): String
    fun appName(context: Any?): String
    fun versionName(context: Any?): String
}
