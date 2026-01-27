package com.zawaro.sleepchad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE dayOfWeek = :day")
    fun getByDay(day: Int): ScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(schedule: ScheduleEntity)

    @Query("SELECT * FROM schedules")
    fun getAll(): List<ScheduleEntity>
}
