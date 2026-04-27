package com.aetheria.bigtype.privacy

import android.text.InputType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrivacyDetector @Inject constructor() {

    fun isSecureField(inputType: Int): Boolean {
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
               variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
               variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
               variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    fun isBankingField(inputType: Int): Boolean {
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        return variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD ||
               (inputType and InputType.TYPE_CLASS_NUMBER) != 0
    }
}