package com.joker.coolmallkmp.core.platform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.joker.coolmallkmp.core.di.AppContainerProvider
import kotlinx.datetime.Clock

private var pendingVerificationCode: String? = null

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
actual fun notifyVerificationCode(code: String) {
    val context = AppContainerProvider.appContext as? Context ?: return
    val channelId = "verification_code_channel"
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(
        NotificationChannel(
            channelId,
            "验证码通知",
            NotificationManager.IMPORTANCE_HIGH,
        ).also {
            it.description = "用于显示短信验证码的通知渠道"
        },
    )
    if (ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
    ) != PackageManager.PERMISSION_GRANTED
    ) {
        pendingVerificationCode = code
        CurrentActivityHolder.current?.requestPermissions(
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE,
        )
        return
    }
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(com.joker.coolmallkmp.shared.R.drawable.ic_logo)
        .setContentTitle("验证码")
        .setContentText("您的验证码是: $code")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(
        ("verification-$code-${Clock.System.now()}").hashCode(),
        notification,
    )
}

private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 2001

fun onVerificationNotificationPermissionResult(granted: Boolean) {
    val code = pendingVerificationCode
    pendingVerificationCode = null
    if (granted && !code.isNullOrBlank()) {
        notifyVerificationCode(code)
    }
}
