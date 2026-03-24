package com.zawaro.sleepchad.data

import androidx.room.*

@Dao
interface CustomAlarmDao {
    @Query("SELECT * FROM custom_alarms")
    fun getCustomAlarms(): List<CustomAlarmEntity>

    @Query("SELECT * FROM custom_alarms WHERE id = :id")
    fun getCustomAlarmById(id: Long): CustomAlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: CustomAlarmEntity): Long

    @Update
    fun update(alarm: CustomAlarmEntity)

    @Delete
    fun delete(alarm: CustomAlarmEntity)

    @Query("DELETE FROM custom_alarms WHERE id = :id")
    fun deleteById(id: Long)
}
