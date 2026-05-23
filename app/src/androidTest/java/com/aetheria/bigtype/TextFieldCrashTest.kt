package com.aetheria.bigtype

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TextFieldCrashTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testTextFieldInteraction() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = androidx.compose.material3.darkColorScheme(
                background = Color(0xFF0D0F1A),
                surface = Color(0xFF1E2235),
                primary = Color(0xFF00E5FF),
                secondary = Color(0xFF0097A7),
                onPrimary = Color(0xFFE8EAF6),
                onSecondary = Color(0xFF7986CB),
                error = Color(0xFFEF5350),
                tertiary = Color(0xFF69F0AE)
            )) {
                androidx.compose.foundation.layout.Column {
                    androidx.compose.material3.Text("Test Keyboard")
                }
            }
        }

        composeTestRule.onNodeWithText("Test Keyboard").performClick()
    }
}