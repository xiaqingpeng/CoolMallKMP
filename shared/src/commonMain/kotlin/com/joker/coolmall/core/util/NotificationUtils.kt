package com.joker.coolmall.core.util

expect object NotificationUtils {
    fun notify(context: Any?, title: String, content: String)
}
