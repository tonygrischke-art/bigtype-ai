package com.aetheria.bigtype.keyboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmojiPredictor @Inject constructor() {
    private val _predictedEmojis = MutableStateFlow<List<String>>(emptyList())
    val predictedEmojis: StateFlow<List<String>> = _predictedEmojis

    fun predict(text: String) {
        _predictedEmojis.value = when {
            text.contains("love", ignoreCase = true) -> listOf("❤️", "😍", "💕")
            text.contains("fire", ignoreCase = true) -> listOf("🔥", "💯", "⚡")
            text.contains("happy", ignoreCase = true) -> listOf("😊", "🎉", "✨")
            text.contains("sad", ignoreCase = true) -> listOf("😢", "💔", "😞")
            text.contains("lol", ignoreCase = true) -> listOf("😂", "🤣", "😆")
            text.contains("thanks", ignoreCase = true) -> listOf("🙏", "👍", "💯")
            else -> emptyList()
        }
    }
}