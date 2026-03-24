package com.zawaro.sleepchad.domain.usecases

import java.util.Calendar

class GetExceptionAlarmsUseCase(
    private val repository: com.zawaro.sleepchad.domain.repository.CustomAlarmRepository,
) {
    suspend operator fun invoke() = repository.getCustomAlarms()
}

class CreateExceptionAlarmUseCase(
    private val repository: com.zawaro.sleepchad.domain.repository.CustomAlarmRepository,
) {
    suspend operator fun invoke(alarm: com.zawaro.sleepchad.data.CustomAlarmEntity): Long = 
        repository.saveCustomAlarm(alarm)
        
    suspend fun createWithTime(
        name: String,
        enabledDaysSet: Set<Int>,
        bedtimeMs: Long?,
        wakeupMs: Long?
    ): Long {
        val calendar = Calendar.getInstance()
        val newBedtime = bedtimeMs ?: run {
            // Default to 10 PM if not provided
            calendar.set(Calendar.HOUR_OF_DAY, 22)
            calendar.set(Calendar.MINUTE, 0)
            calendar.timeInMillis
        }
        
        val entity = com.zawaro.sleepchad.data.CustomAlarmEntity(
            id = 0L,
            name = name,
            enabledDaysString = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysString(enabledDaysSet),
            bedtimeMs = newBedtime,
            wakeupMs = wakeupMs ?: Calendar.getInstance().run {
                set(Calendar.HOUR_OF_DAY, 7)
                set(Calendar.MINUTE, 0)
                timeInMillis
            }
        )
        
        return repository.saveCustomAlarm(entity)
    }
}

class DeleteExceptionAlarmUseCase(
    private val repository: com.zawaro.sleepchad.domain.repository.CustomAlarmRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteCustomAlarmById(id)
}
