package com.zawaro.sleepchad.data

import androidx.room.*

@Dao
interface ErrandDao {
    @Query("SELECT * FROM errands WHERE alarmId = :alarmId")
    fun getErrandsByAlarm(alarmId: Long): List<ErrandEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(errand: ErrandEntity): Long

    @Query("DELETE FROM errands WHERE id = :id")
    fun deleteById(id: Long)

    @Delete
    fun delete(errand: ErrandEntity)
}