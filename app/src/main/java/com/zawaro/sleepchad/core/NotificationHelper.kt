package com.zawaro.sleepchad.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    const val CHANNEL_ERRANDS = "channel_errands"
    const val CHANNEL_BEDTIME = "channel_bedtime"
    const val CHANNEL_WAKEUP = "channel_wakeup"
    const val WAKE_UP_CHANNEL = "wake_up_channel"

    fun createChannels(context: Context) {
        val channels =
            listOf(
                NotificationChannel(CHANNEL_ERRANDS, "Errands", NotificationManager.IMPORTANCE_DEFAULT),
                NotificationChannel(CHANNEL_BEDTIME, "Bedtime", NotificationManager.IMPORTANCE_DEFAULT),
                NotificationChannel(CHANNEL_WAKEUP, "Wakeup", NotificationManager.IMPORTANCE_DEFAULT),
                NotificationChannel(WAKE_UP_CHANNEL, "Wake Up Actions", NotificationManager.IMPORTANCE_HIGH),
            )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channels.forEach { channel ->
            if (manager.getNotificationChannel(channel.id) == null) {
                manager.createNotificationChannel(channel)
            }
        }
    }

    private var notificationIdCounter = 0

    fun show(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        notificationId: Int? = null,
        contentIntent: PendingIntent? = null,
    ) {
        val id = notificationId ?: (notificationIdCounter++ % 10000)
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            val notification =
                NotificationCompat
                    .Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build()
            try {
                NotificationManagerCompat.from(context).notify(id, notification)
            } catch (e: SecurityException) {
            }
        }
    }
}
