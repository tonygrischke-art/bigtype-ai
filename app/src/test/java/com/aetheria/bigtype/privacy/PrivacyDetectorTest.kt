package com.aetheria.bigtype.privacy

import android.text.InputType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyDetectorTest {
    private val detector = PrivacyDetector()

    @Test
    fun `password input types are secure`() {
        assertTrue(detector.isSecureField(
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD))
        assertTrue(detector.isSecureField(
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD))
        assertTrue(detector.isSecureField(
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD))
    }

    @Test
    fun `plain text input types are not secure`() {
        assertFalse(detector.isSecureField(InputType.TYPE_CLASS_TEXT))
        assertFalse(detector.isBankingField(InputType.TYPE_CLASS_TEXT))
    }

    @Test
    fun `numeric password is treated as banking`() {
        assertTrue(detector.isBankingField(
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD))
    }
}
