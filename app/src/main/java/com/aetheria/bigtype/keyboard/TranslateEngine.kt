package com.aetheria.bigtype.keyboard

import com.aetheria.bigtype.llm.LLMClient




class TranslateEngine constructor(
    private val llmClient: LLMClient
) {
    suspend fun translate(text: String, targetLang: String): String {
        val prompt = "Translate to $targetLang, reply ONLY with the translation: $text"
        return llmClient.getCompletions(prompt).firstOrNull() ?: ""
    }
}