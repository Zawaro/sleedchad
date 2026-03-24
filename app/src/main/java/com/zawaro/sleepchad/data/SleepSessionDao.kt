package com.zawaro.sleepchad.data

import androidx.room.*

@Dao
interface SleepSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(session: SleepSessionEntity): Long

    @Query("SELECT * FROM sleep_sessions WHERE date = :date ORDER BY id DESC LIMIT 1")
    fun getSessionByDate(date: String): SleepSessionEntity?

    @Update
    fun update(session: SleepSessionEntity)
}
