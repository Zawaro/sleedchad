package com.zawaro.sleepchad.presentation.schedule

import android.app.Application
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ScheduleScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun testTheme(content: @Composable () -> Unit) {
        MaterialTheme { content() }
    }

    @Test
    fun scheduleScreenShowsAllDays() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = SettingsViewModel(context)
        composeTestRule.setContent {
            testTheme { ScheduleScreen(viewModel) }
        }
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
            composeTestRule.onNodeWithText(day, useUnmergedTree = true).assertExists()
        }
    }

    @Test
    fun openingSettingsAndChangingThemeUpdatesViewmodel() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = SettingsViewModel(context)
        composeTestRule.setContent { testTheme { ScheduleScreen(viewModel) } }
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
    fun aboutScreenNavigatesBackToSchedule() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = SettingsViewModel(context)
        composeTestRule.setContent { testTheme { ScheduleScreen(viewModel) } }
        composeTestRule.onNode(hasClickAction()).performClick()
        composeTestRule.onNodeWithText("About").performClick()
        composeTestRule.onNodeWithText("Done").performClick()
        composeTestRule.onNodeWithText("Mon", useUnmergedTree = true).assertExists()
    }
}
