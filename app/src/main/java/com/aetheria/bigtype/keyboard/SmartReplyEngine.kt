package com.aetheria.bigtype.keyboard

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SmartReply(
    val text: String,
    val type: ReplyType
)

enum class ReplyType { GREETING, QUESTION, CODE, ACKNOWLEDGE }

class SmartReplyEngine @Inject constructor() {
    private val _replies = MutableStateFlow<List<SmartReply>>(emptyList())
    val replies: StateFlow<List<SmartReply>> = _replies
    
    fun analyzeAndSuggest(text: String) {
        val lower = text.lowercase()
        val reply = when {
            lower.contains("?") -> SmartReply("Let me help with that!", ReplyType.QUESTION)
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> SmartReply("Hey there! 👋", ReplyType.GREETING)
            lower.contains("thanks") || lower.contains("thank you") -> SmartReply("You're welcome!", ReplyType.ACKNOWLEDGEMENT)
            lower.contains("function") || lower.contains("def ") || lower.contains("class ") -> SmartReply("Here's what that does:", ReplyType.CODE)
            else -> SmartReply("Got it", ReplyType.ACKNOWLEDGEMENT)
        }
        _replies.value = listOf(reply, SmartReply("Thanks!", ReplyType.ACKNOWLEDGEMENT), SmartReply("Help me", ReplyType.QUESTION))
    }
    
    fun clear() { _replies.value = emptyList() }
}