package com.aetheria.bigtype

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.AndroidJUnit4

class TextFieldCrashTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTextFieldClickDoesNotCrash() {
        // Test that clicking text field doesn't crash
        composeTestRule.setContent {
            BigTypeKeyboardScreen(
                onTextInput = { text ->
                    val ic = currentInputConnection
                    ic?.commitText(text, 1)
                },
                onDelete = {
                    val ic = currentInputConnection
                    ic?.deleteSurroundingText(1, 0)
                }
            )
        }
        
        // Try to interact with the UI to see if it crashes
        // Add specific test for text field interaction
    }
}