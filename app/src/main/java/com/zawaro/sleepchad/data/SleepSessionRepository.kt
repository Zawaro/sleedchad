package com.zawaro.sleepchad.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SleepSessionRepository(
    private val sleepSessionDao: SleepSessionDao,
) {
    companion object {
        fun createWithContext(context: android.content.Context): SleepSessionRepository = SleepSessionRepository(
            AppDatabase.getInstance(context).sleepSessionDao()
        )
    }

    suspend fun insert(session: SleepSessionEntity): Long = withContext(Dispatchers.IO) {
        sleepSessionDao.insert(session)
    }

    suspend fun getByDate(date: String): SleepSessionEntity? = withContext(Dispatchers.IO) {
        sleepSessionDao.getSessionByDate(date)
    }

    fun update(session: SleepSessionEntity) {
        sleepSessionDao.update(session)
    }
}
