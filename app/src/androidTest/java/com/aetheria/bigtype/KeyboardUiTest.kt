package com.aetheria.bigtype

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aetheria.bigtype.ui.BigTypeKeyboardScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeyboardUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun testKeyboardDisplaysKeys() {
        composeTestRule.setContent {
            BigTypeKeyboardScreen(
                viewModel = com.aetheria.bigtype.keyboard.KeyboardViewModel(
                    llmClient = com.aetheria.bigtype.llm.LLMClient(),
                    bridgeClient = com.aetheria.bigtype.bridge.BridgeClient(),
                    modifierManager = com.aetheria.bigtype.keyboard.ModifierStateManager()
                ),
                onTextInput = {},
                onDelete = {},
                onKeyEvent = {}
            )
        }

        composeTestRule.onNodeWithText("q").assertIsDisplayed()
        composeTestRule.onNodeWithText("p").assertIsDisplayed()
        composeTestRule.onNodeWithText("a").assertIsDisplayed()
    }
}
