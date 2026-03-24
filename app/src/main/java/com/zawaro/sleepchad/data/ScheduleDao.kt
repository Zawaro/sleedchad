package com.zawaro.sleepchad.data

import androidx.room.*

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE isDefaultAlarm = 1 LIMIT 1")
    fun getDefaultSchedule(): ScheduleEntity?

    @Query("SELECT * FROM schedules WHERE isDefaultAlarm = 0")
    fun getExceptionSchedules(): List<ScheduleEntity>

    @Query("SELECT * FROM schedules WHERE id = :id")
    fun getById(id: Long): ScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(schedule: ScheduleEntity): Long

    @Update
    fun update(schedule: ScheduleEntity)

    @Delete
    fun delete(schedule: ScheduleEntity)

    @Query("DELETE FROM schedules WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getUserPreferences(): UserPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserPreferences(preferences: UserPreferencesEntity): Long

    @Update
    fun updateUserPreferences(preferences: UserPreferencesEntity)

    @Query("SELECT * FROM custom_alarms")
    fun getCustomAlarms(): List<CustomAlarmEntity>

    @Query("SELECT * FROM custom_alarms WHERE id = :id")
    fun getCustomAlarmById(id: Long): CustomAlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomAlarm(alarm: CustomAlarmEntity): Long

    @Update
    fun updateCustomAlarm(alarm: CustomAlarmEntity)

    @Delete
    fun deleteCustomAlarm(alarm: CustomAlarmEntity)

    @Query("DELETE FROM custom_alarms WHERE id = :id")
    fun deleteCustomAlarmById(id: Long)
}
