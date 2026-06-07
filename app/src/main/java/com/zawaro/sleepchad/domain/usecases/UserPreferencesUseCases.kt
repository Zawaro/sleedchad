package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke() = repository.getUserPreferences()
    
    suspend fun save(preferences: com.zawaro.sleepchad.data.UserPreferencesEntity) = 
        repository.saveUserPreferences(preferences)
}

class SaveUserPreferencesUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(preferences: com.zawaro.sleepchad.data.UserPreferencesEntity): Long = 
        repository.saveUserPreferences(preferences)
        
    suspend fun update(preferences: com.zawaro.sleepchad.data.UserPreferencesEntity) = 
        repository.updateUserPreferences(preferences)
}
