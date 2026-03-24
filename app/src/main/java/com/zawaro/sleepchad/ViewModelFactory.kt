package com.zawaro.sleepchad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.app.Application
import com.zawaro.sleepchad.core.NotificationHelper
import com.zawaro.sleepchad.data.ErrandRepository
import com.zawaro.sleepchad.data.ScheduleRepository
import com.zawaro.sleepchad.domain.usecases.GetScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.SaveScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.ScheduleAlarmsUseCase
import com.zawaro.sleepchad.presentation.schedule.ScheduleViewModel

class ViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            val repository = ScheduleRepository.createWithContext(application)
            val errandRepository = ErrandRepository(repository.errandDao)
            val getSchedule = GetScheduleUseCase(repository)
            val saveSchedule = SaveScheduleUseCase(repository, errandRepository)
            val scheduleAlarms = ScheduleAlarmsUseCase(application, repository)
            
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(getSchedule, saveSchedule, scheduleAlarms, errandRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

