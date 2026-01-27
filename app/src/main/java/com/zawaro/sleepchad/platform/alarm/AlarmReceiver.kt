package com.zawaro.sleepchad.platform.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zawaro.sleepchad.core.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val TYPE_ERRANDS = 1
        const val TYPE_BEDTIME = 2
        const val TYPE_WAKEUP   = 3

        fun intent(context: Context, type: Int, message: String): Intent =
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra(EXTRA_TYPE, type)
                putExtra(EXTRA_MESSAGE, message)
            }

        private const val EXTRA_TYPE = "type"
        private const val EXTRA_MESSAGE = "message"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val msg = intent.getStringExtra(EXTRA_MESSAGE) ?: return
        NotificationHelper.show(
            context,
            when (intent.getIntExtra(EXTRA_TYPE, 0)) {
                TYPE_ERRANDS -> NotificationHelper.CHANNEL_ERRANDS
                TYPE_BEDTIME -> NotificationHelper.CHANNEL_BEDTIME
                else -> NotificationHelper.CHANNEL_WAKEUP
            },
            "SleepChad",
            msg
        )
    }
}
