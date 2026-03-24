package com.zawaro.sleepchad.domain.repository

import com.zawaro.sleepchad.data.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomAlarmRepository(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend fun getCustomAlarms(): List<com.zawaro.sleepchad.data.CustomAlarmEntity> = withContext(Dispatchers.IO) {
        scheduleRepository.getCustomAlarms()
    }
    
    suspend fun getCustomAlarmById(id: Long): com.zawaro.sleepchad.data.CustomAlarmEntity? = withContext(Dispatchers.IO) {
        scheduleRepository.getCustomAlarmById(id)
    }
    
    suspend fun saveCustomAlarm(alarm: com.zawaro.sleepchad.data.CustomAlarmEntity): Long = withContext(Dispatchers.IO) {
        scheduleRepository.saveCustomAlarm(alarm)
    }
    
    suspend fun updateCustomAlarm(alarm: com.zawaro.sleepchad.data.CustomAlarmEntity) = withContext(Dispatchers.IO) {
        scheduleRepository.updateCustomAlarm(alarm)
    }
    
    suspend fun deleteCustomAlarm(alarm: com.zawaro.sleepchad.data.CustomAlarmEntity) = withContext(Dispatchers.IO) {
        scheduleRepository.deleteCustomAlarm(alarm)
    }
    
    suspend fun deleteCustomAlarmById(id: Long) = withContext(Dispatchers.IO) {
        scheduleRepository.deleteCustomAlarmById(id)
    }
}
