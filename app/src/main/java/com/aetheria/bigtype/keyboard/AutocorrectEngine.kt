package com.aetheria.bigtype.keyboard





class AutocorrectEngine constructor() {
    private val commonFixes = mapOf(
        "teh" to "the", "adn" to "and", "hte" to "the",
        "dont" to "don't", "cant" to "can't", "wont" to "won't",
        "ive" to "I've", "im" to "I'm", "id" to "I'd"
    )

    fun correct(word: String): String {
        return commonFixes[word.lowercase()] ?: word
    }
}