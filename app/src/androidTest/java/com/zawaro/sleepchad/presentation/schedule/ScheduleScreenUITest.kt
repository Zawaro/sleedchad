package com.zawaro.sleepchad.presentation.schedule

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel

class ScheduleScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun TestTheme(content: @Composable () -> Unit) {
        MaterialTheme { content() }
    }

    @Test
    fun schedule_screen_shows_all_days() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = SettingsViewModel(context)
        composeTestRule.setContent {
            TestTheme { ScheduleScreen(viewModel) }
        }
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
            composeTestRule.onNodeWithText(day, useUnmergedTree = true).assertExists()
        }
    }

    @Test
    fun opening_settings_and_changing_theme_updates_viewmodel() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = SettingsViewModel(context)
        composeTestRule.setContent { TestTheme { ScheduleScreen(viewModel) } }
        // Open menu
        composeTestRule.onNode(hasClickAction()).performClick()
        // Click Settings title
        composeTestRule.onNodeWithText("Settings").assertExists()
        composeTestRule.onNodeWithText("Done").performClick() // just close settings for simplicity
        // Now open menu again and change theme
        composeTestRule.onNode(hasClickAction()).performClick()
        composeTestRule.onNodeWithText("Settings").performClick()
        // Click current theme row (e.g., "System default")
        composeTestRule.onNodeWithText("System default", useUnmergedTree = true).performClick()
        // Select Light
        composeTestRule.onNodeWithText("Light").performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        assertEquals(1, viewModel.themeIndex.intValue)
    }

    @Test
    fun about_screen_navigates_back_to_schedule() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = SettingsViewModel(context)
        composeTestRule.setContent { TestTheme { ScheduleScreen(viewModel) } }
        composeTestRule.onNode(hasClickAction()).performClick()
        composeTestRule.onNodeWithText("About").performClick()
        composeTestRule.onNodeWithText("Done").performClick()
        composeTestRule.onNodeWithText("Mon", useUnmergedTree = true).assertExists()
    }
}
