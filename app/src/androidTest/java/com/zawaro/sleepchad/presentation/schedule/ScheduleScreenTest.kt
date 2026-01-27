package com.zawaro.sleepchad.presentation.schedule

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalFoundationApi::class)
class ScheduleScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun TestTheme(content: @Composable () -> Unit) {
        MaterialTheme { content() }
    }

    @Test
    fun menu_opens_settings_and_closes() {
        composeTestRule.setContent { TestTheme { ScheduleScreen() } }
        // No Settings text initially
        composeTestRule.onNodeWithText("Settings", useUnmergedTree = true).assertDoesNotExist()
        // Click menu icon
        composeTestRule.onAllNodes(hasIcon(Icons.Default.MoreVert)).first().performClick()
        // Settings should appear
        composeTestRule.onNodeWithText("Settings").assertExists()
        // Press Done button to close settings
        composeTestRule.onNodeWithText("Done").performClick()
        // Back on schedule screen (day rows exist)
        composeTestRule.onAllNodes(hasText("Mon"), useUnmergedTree = true).first().assertExists()
    }

    @Test
    fun menu_opens_about_and_closes() {
        composeTestRule.setContent { TestTheme { ScheduleScreen() } }
        // Open menu and select About
        composeTestRule.onAllNodes(hasIcon(Icons.Default.MoreVert)).first().performClick()
        composeTestRule.onNodeWithText("About").assertExists()
        // Press Done button to close about
        composeTestRule.onNodeWithText("Done").performClick()
        // Verify back on schedule screen
        composeTestRule.onAllNodes(hasText("Mon"), useUnmergedTree = true).first().assertExists()
    }

    @Test
    fun day_row_displays_information() {
        composeTestRule.setContent { TestTheme { ScheduleScreen() } }
        // Find a row by day label
        val monNode = composeTestRule.onAllNodes(hasText("Mon"), useUnmergedTree = true).first()
        monNode.assertExists()
        // Check for Prep and Bedtime texts
        composeTestRule.onAllNodes(hasText(startsWith("Prep alarm:"))).first().assertExists()
        composeTestRule.onAllNodes(hasText(startsWith("Bedtime alarm:"))).first().assertExists()
    }
}
