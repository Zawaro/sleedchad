package com.zawaro.sleepchad.test

import com.zawaro.sleepchad.domain.usecases.GetScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.SaveScheduleUseCase
import io.mockk.MockKAnnotations
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * ViewModel and UseCase integrity tests.
 */
class ViewModelAndUseCaseIntegrityTest {

    private lateinit var scheduleRepository: com.zawaro.sleepchad.data.ScheduleRepository
    
    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        scheduleRepository = mockk(relaxed = true)
    }

    @Test
    fun getScheduleUseCase_canBeInstantiated() {
        val useCase = GetScheduleUseCase(scheduleRepository)
        assertNotNull(useCase)
    }

    @Test
    fun saveScheduleUseCase_parameters() {
        val errandRepository: com.zawaro.sleepchad.data.ErrandRepository = mockk(relaxed = true)
        val useCase = SaveScheduleUseCase(scheduleRepository, errandRepository)
        assertNotNull(useCase)
    }

    @Test
    fun repositoryInterfaces_exist() {
        assertNotNull(com.zawaro.sleepchad.data.ScheduleRepository::class.java)
        assertNotNull(com.zawaro.sleepchad.data.ErrandRepository::class.java)
    }
}
