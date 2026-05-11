package com.aetheria.bigtype

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aetheria.bigtype.ime.BigTypeIMEService
import com.aetheria.bigtype.ui.BigTypeKeyboardScreen
import org.junit.Rule
import org.junit.Test

class KeyboardCrashTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTextFieldInteraction() {
        // Test the keyboard functionality
        composeTestRule.setContent {
            BigTypeKeyboardScreen(
                onTextInput = { text ->
                    // Simple implementation for test
                },
                onDelete = {
                    // Simple implementation for test
                },
                onKeyEvent = { keyCode ->
                    // Simple implementation for test
                }
            )
        }
        
        // Try to interact with a text field to see if it crashes
        composeTestRule.onNodeWithText("some text field").performClick()
    }
}