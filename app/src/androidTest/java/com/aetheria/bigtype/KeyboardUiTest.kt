package com.aetheria.bigtype

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aetheria.bigtype.ui.BigTypeKeyboardScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class KeyboardUiTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testKeyboardDisplaysKeys() {
        composeTestRule.setContent {
            BigTypeKeyboardScreen(
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