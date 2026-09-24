package com.aetheria.bigtype.keyboard

import android.util.Log
import com.aetheria.bigtype.clipboard.BigTypeDatabase

/**
 * Autocorrect with Room-backed learning.
 * Falls back to the static 9-word dictionary when no DB is available
 * (e.g. unit tests, pre-app-init IME contexts).
 */
class AutocorrectEngine(
    private val db: BigTypeDatabase? = null,
    private val userId: String = "default"
) {
    private val staticFixes = mapOf(
        "teh" to "the", "adn" to "and", "hte" to "the",
        "dont" to "don't", "cant" to "can't", "wont" to "won't",
        "ive" to "I've", "im" to "I'm", "id" to "I'd"
    )

    // Fast cache of top-frequency rules; refreshed on write
    @Volatile
    private var topCache: Map<String, String> = emptyMap()

    suspend fun warmCache() {
        val d = db ?: return
        topCache = d.autocorrectDao().getTopByFreq()
            .associate { it.fromWord to it.toWord }
    }

    /** Synchronous path (IME hot path): cache then static dict. */
    fun correct(word: String): String {
        val key = word.lowercase()
        return topCache[key] ?: staticFixes[key] ?: word
    }

    /** Async path: full DB lookup. */
    suspend fun correctAsync(word: String): String {
        val key = word.lowercase()
        val rule = db?.autocorrectDao()?.getByWord(key)
        return rule?.toWord ?: correct(word)
    }

    /** User accepted our correction - bump its frequency. */
    suspend fun learnFromAcceptance(original: String) {
        val d = db ?: return
        d.autocorrectDao().incrementFreq(original.lowercase())
        warmCache()
    }

    /** User rejected our correction (deleted it) - lower its frequency. */
    suspend fun learnFromDeletion(original: String) {
        val d = db ?: return
        d.autocorrectDao().decrementFreq(original.lowercase())
        d.autocorrectDao().purgeZeroFreq(original.lowercase())
        Log.d(TAG, "Learned: reduce corrections for '$original'")
        warmCache()
    }

    suspend fun addCustomRule(from: String, to: String, appId: String? = null) {
        val d = db ?: return
        d.autocorrectDao().insert(
            AutocorrectRuleEntity(
                fromWord = from.lowercase(),
                toWord = to,
                appId = appId,
                frequency = 1,
                isUserAdded = true
            )
        )
        warmCache()
    }

    companion object {
        private const val TAG = "BigType"
    }
}
