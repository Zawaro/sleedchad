package com.zawaro.sleepchad.platform.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zawaro.sleepchad.MainActivity
import com.zawaro.sleepchad.core.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val TYPE_ERRANDS = 1
        const val TYPE_BEDTIME = 2
        const val TYPE_WAKEUP = 3

        const val EXTRA_DEEP_LINK_ACTION = "deep_link_action"
        const val ACTION_OPEN_SCHEDULE = "open_schedule"
        const val ACTION_RECORD_WAKEUP = "record_wakeup"

        fun intent(
            context: Context,
            type: Int,
            message: String,
        ): Intent =
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra(EXTRA_TYPE, type)
                putExtra(EXTRA_MESSAGE, message)
            }

        private const val EXTRA_TYPE = "type"
        private const val EXTRA_MESSAGE = "message"

        fun intent(
            context: Context,
            type: Int,
            message: String,
            alarmId: Long,
            label: String,
        ): Intent =
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra(EXTRA_TYPE, type)
                putExtra(EXTRA_MESSAGE, message)
                putExtra(EXTRA_ALARM_ID, alarmId)
                putExtra(EXTRA_LABEL, label)
            }

        private const val EXTRA_ALARM_ID = "alarm_id"
        private const val EXTRA_LABEL = "label"
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val msg = intent.getStringExtra(EXTRA_MESSAGE) ?: return
        val type = intent.getIntExtra(EXTRA_TYPE, 0)
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, 0L)

        val title = when (type) {
            TYPE_BEDTIME -> "Time to wind down"
            TYPE_WAKEUP -> "Good morning!"
            else -> "SleepChad"
        }

        val deepLinkAction = when (type) {
            TYPE_BEDTIME -> ACTION_OPEN_SCHEDULE
            TYPE_WAKEUP -> ACTION_RECORD_WAKEUP
            else -> null
        }

        var contentIntent: PendingIntent? = null
        if (deepLinkAction != null) {
            val activityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_DEEP_LINK_ACTION, deepLinkAction)
            }
            contentIntent = PendingIntent.getActivity(
                context,
                (type * 1000 + alarmId).toInt().and(0x7FFFFFFF),
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        NotificationHelper.show(
            context,
            when (type) {
                TYPE_ERRANDS -> NotificationHelper.CHANNEL_ERRANDS
                TYPE_BEDTIME -> NotificationHelper.CHANNEL_BEDTIME
                else -> NotificationHelper.CHANNEL_WAKEUP
            },
            title,
            msg,
            notificationId = (type * 1000 + alarmId).toInt().and(Int.MAX_VALUE),
            contentIntent = contentIntent
        )
    }
}
