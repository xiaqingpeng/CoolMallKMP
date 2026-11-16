package com.joker.coolmallkmp.core.util

import platform.Foundation.NSLog

actual object LogUtils {
    actual fun d(tag: String, message: String) { NSLog("D/%s: %s", tag, message) }
    actual fun w(tag: String, message: String) { NSLog("W/%s: %s", tag, message) }
    actual fun e(tag: String, message: String, throwable: Throwable?) {
        NSLog("E/%s: %s %s", tag, message, throwable?.message.orEmpty())
    }
}
