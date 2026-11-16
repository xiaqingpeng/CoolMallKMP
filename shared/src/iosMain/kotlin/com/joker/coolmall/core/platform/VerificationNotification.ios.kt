package com.joker.coolmallkmp.core.platform

import kotlinx.cinterop.BetaInteropApi
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

@OptIn(BetaInteropApi::class)
actual fun notifyVerificationCode(code: String) {
    val center = UNUserNotificationCenter.currentNotificationCenter()
    center.requestAuthorizationWithOptions(
        options = UNAuthorizationOptionAlert or
            UNAuthorizationOptionSound or
            UNAuthorizationOptionBadge,
        completionHandler = { _, _ -> },
    )
    val content = UNMutableNotificationContent().apply {
        setTitle("验证码")
        setBody("您的验证码是: $code")
        setSound(UNNotificationSound.defaultSound)
    }
    val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
        timeInterval = 1.0,
        repeats = false,
    )
    val request = UNNotificationRequest.requestWithIdentifier(
        identifier = "verification-${code.hashCode()}-${code.length}",
        content = content,
        trigger = trigger,
    )
    center.addNotificationRequest(request, withCompletionHandler = { _ -> })
}
