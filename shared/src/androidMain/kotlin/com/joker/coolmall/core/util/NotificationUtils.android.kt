package com.joker.coolmall.core.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

actual object NotificationUtils {
    actual fun notify(context: Any?, title: String, content: String) {
        val ctx = context as? Context ?: return
        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val channelId = "coolmall_common"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "CoolMall", NotificationManager.IMPORTANCE_DEFAULT),
            )
        }
        manager.notify(
            content.hashCode(),
            NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .build(),
        )
    }
}
