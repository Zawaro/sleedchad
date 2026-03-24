package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.domain.repository.CustomAlarmRepository

class GetCustomAlarmsUseCase(
    private val repository: CustomAlarmRepository,
) {
    suspend operator fun invoke() = repository.getCustomAlarms()
    
    suspend fun getById(id: Long) = repository.getCustomAlarmById(id)
}

class SaveCustomAlarmUseCase(
    private val repository: CustomAlarmRepository,
) {
    suspend operator fun invoke(alarm: com.zawaro.sleepchad.data.CustomAlarmEntity): Long = 
        repository.saveCustomAlarm(alarm)
        
    suspend fun update(alarm: com.zawaro.sleepchad.data.CustomAlarmEntity) = 
        repository.updateCustomAlarm(alarm)
        
    suspend fun delete(alarm: com.zawaro.sleepchad.data.CustomAlarmEntity) = 
        repository.deleteCustomAlarm(alarm)
        
    suspend fun deleteById(id: Long) = 
        repository.deleteCustomAlarmById(id)
}
