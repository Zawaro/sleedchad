package com.zawaro.sleepchad.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    const val CHANNEL_ERRANDS = "channel_errands"
    const val CHANNEL_BEDTIME = "channel_bedtime"
    const val CHANNEL_WAKEUP = "channel_wakeup"

    fun createChannels(context: Context) {
        val channels =
            listOf(
                NotificationChannel(CHANNEL_ERRANDS, "Errands", NotificationManager.IMPORTANCE_DEFAULT),
                NotificationChannel(CHANNEL_BEDTIME, "Bedtime", NotificationManager.IMPORTANCE_DEFAULT),
                NotificationChannel(CHANNEL_WAKEUP, "Wakeup", NotificationManager.IMPORTANCE_DEFAULT),
            )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channels.forEach { channel ->
            if (manager.getNotificationChannel(channel.id) == null) {
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun show(
        context: Context,
        channelId: String,
        title: String,
        message: String,
    ) {
        // Ensure POST_NOTIFICATIONS permission is granted before notifying
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
                    .build()
            try {
                NotificationManagerCompat.from(context).notify(0, notification)
            } catch (e: SecurityException) {
                // Handle potential security exception gracefully
            }
        }
    }
}
