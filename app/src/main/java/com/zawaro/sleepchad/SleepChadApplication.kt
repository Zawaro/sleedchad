package com.zawaro.sleepchad

import android.app.Application
import com.zawaro.sleepchad.core.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SleepChadApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
    }
}
