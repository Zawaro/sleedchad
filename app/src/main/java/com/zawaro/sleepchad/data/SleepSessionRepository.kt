package com.zawaro.sleepchad.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepSessionRepository @Inject constructor(
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
        sleepSessionDao.getSessionByDate(date).first()
    }

    suspend fun getSessionsByDateRange(startMillis: Long, endMillis: Long): List<SleepSessionEntity> = withContext(Dispatchers.IO) {
        sleepSessionDao.getSessionsByDateRange(startMillis, endMillis)
    }

    suspend fun update(session: SleepSessionEntity) = withContext(Dispatchers.IO) {
        sleepSessionDao.update(session)
    }
}
