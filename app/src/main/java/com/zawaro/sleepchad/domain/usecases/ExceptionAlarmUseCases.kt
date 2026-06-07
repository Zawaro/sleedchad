package com.zawaro.sleepchad.domain.usecases

import javax.inject.Inject

class GetExceptionAlarmsUseCase @Inject constructor(
    private val repository: com.zawaro.sleepchad.domain.repository.CustomAlarmRepository,
) {
    suspend operator fun invoke() = repository.getCustomAlarms()
}

class CreateExceptionAlarmUseCase @Inject constructor(
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
        val newBedtime = bedtimeMs ?: (22 * 3600000L) // Default to 10 PM as time-of-day ms

        val entity = com.zawaro.sleepchad.data.CustomAlarmEntity(
            id = 0L,
            name = name,
            enabledDaysString = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysString(enabledDaysSet),
            bedtimeMs = newBedtime,
            wakeupMs = wakeupMs ?: (7 * 3600000L) // Default to 7 AM as time-of-day ms
        )

        return repository.saveCustomAlarm(entity)
    }
}

class DeleteExceptionAlarmUseCase @Inject constructor(
    private val repository: com.zawaro.sleepchad.domain.repository.CustomAlarmRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteCustomAlarmById(id)
}
