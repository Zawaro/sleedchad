package com.zawaro.sleepchad.presentation.welcome

import androidx.compose.ui.test.junit4.createComposeRule
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class WelcomeScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun placeholder() {
        // UI tests require an emulator. Run manually with `./gradlew :app:connectedCheck`
    }
}
