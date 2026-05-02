package com.aetheria.bigtype

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.aetheria.bigtype.ui.BigTypeKeyboardScreen
import org.junit.Rule
import org.junit.Test

class KeyboardUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testKeyboardDisplaysKeys() {
        composeTestRule.setContent {
            // In a real scenario, we'd mock the ViewModel or use Hilt
            // For this exercise, we're verifying the UI can be set up
            BigTypeKeyboardScreen(
                onTextInput = {},
                onDelete = {},
                onKeyEvent = {}
            )
        }

        // Check if some basic keys are displayed
        composeTestRule.onNodeWithText("q").assertIsDisplayed()
        composeTestRule.onNodeWithText("p").assertIsDisplayed()
        composeTestRule.onNodeWithText("a").assertIsDisplayed()
    }
}
