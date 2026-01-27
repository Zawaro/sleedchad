package com.zawaro.sleepchad.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository that abstracts access to [ScheduleEntity] data.
 * It uses the Room database under the hood but can be mocked easily for tests.
 */
class ScheduleRepository(private val dao: ScheduleDao) {
    companion object {
        fun createWithContext(context: Context): ScheduleRepository =
            ScheduleRepository(AppDatabase.getInstance(context).scheduleDao())
    }

    /** Returns the schedule for a specific weekday (1‑7). */
    suspend fun getScheduleForDay(dayOfWeek: Int): ScheduleEntity? =
        withContext(Dispatchers.IO) { dao.getByDay(dayOfWeek) }

    /** Returns all schedules. */
    suspend fun getAllSchedules(): List<ScheduleEntity> =
        withContext(Dispatchers.IO) { dao.getAll() }

    /** Persists (inserts or replaces) a schedule entry. */
    suspend fun saveSchedule(schedule: ScheduleEntity) {
        withContext(Dispatchers.IO) { dao.insertOrReplace(schedule) }
    }
}
