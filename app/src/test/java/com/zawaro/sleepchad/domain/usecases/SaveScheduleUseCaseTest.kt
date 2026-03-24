package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.ErrandDao
import com.zawaro.sleepchad.data.ErrandRepository
import com.zawaro.sleepchad.data.ScheduleEntity
import com.zawaro.sleepchad.data.ScheduleRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class SaveScheduleUseCaseTest {

    @MockK
    private lateinit var scheduleRepository: ScheduleRepository
    
    @MockK
    private lateinit var errandDao: ErrandDao

    private lateinit var saveSchedule: SaveScheduleUseCase
    private lateinit var errandRepository: ErrandRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        errandRepository = ErrandRepository(errandDao)
        saveSchedule = SaveScheduleUseCase(scheduleRepository, errandRepository)
    }
}
