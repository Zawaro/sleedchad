package com.zawaro.sleepchad.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(session: SleepSessionEntity): Long

    @Query("SELECT * FROM sleep_sessions WHERE date = :date ORDER BY id DESC LIMIT 1")
    fun getSessionByDate(date: String): Flow<SleepSessionEntity?>

    @Update
    fun update(session: SleepSessionEntity)

    @Query("SELECT * FROM sleep_sessions WHERE wakeUpTimeMs >= :startMillis AND wakeUpTimeMs <= :endMillis ORDER BY wakeUpTimeMs ASC")
    fun getSessionsByDateRange(startMillis: Long, endMillis: Long): List<SleepSessionEntity>
}
