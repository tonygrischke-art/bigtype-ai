package com.aetheria.bigtype

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.aetheria.bigtype.ui.BigTypeKeyboardScreen
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class KeyboardUiTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun testKeyboardDisplaysKeys() {
        hiltRule.inject()
        composeTestRule.setContent {
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
