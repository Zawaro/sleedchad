package com.zawaro.sleepchad.di

import android.content.Context
import androidx.room.Room
import com.zawaro.sleepchad.data.AppDatabase
import com.zawaro.sleepchad.data.CustomAlarmDao
import com.zawaro.sleepchad.data.ErrandDao
import com.zawaro.sleepchad.data.ScheduleDao
import com.zawaro.sleepchad.data.SleepSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "sleepchad_db",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideScheduleDao(database: AppDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    fun provideErrandDao(database: AppDatabase): ErrandDao = database.errandDao()

    @Provides
    fun provideCustomAlarmDao(database: AppDatabase): CustomAlarmDao = database.customAlarmDao()

    @Provides
    fun provideSleepSessionDao(database: AppDatabase): SleepSessionDao = database.sleepSessionDao()
}
