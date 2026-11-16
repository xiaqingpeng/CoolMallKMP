package com.joker.coolmall.core.util

actual object NotificationUtils {
    actual fun notify(context: Any?, title: String, content: String) {
        // iOS 验证码通知由现有 VerificationNotification 平台实现负责。
    }
}
