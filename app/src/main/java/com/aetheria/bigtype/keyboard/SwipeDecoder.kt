package com.aetheria.bigtype.keyboard

/**
 * Swipe/glide word decoder - inspired by FlorisBoard's SwipeTyping approach
 * (florisboard/florisboard, Apache-2.0): instead of just concatenating touched
 * keys, score dictionary words against the glide path and return ranked guesses.
 */
class SwipeDecoder(
    private val lexicon: Collection<String> = CoreEnglishLexicon.WORDS
) {
    private val path = StringBuilder()

    fun onKey(keyLabel: String) {
        path.append(keyLabel.lowercase())
    }

    fun reset() = path.clear()

    /**
     * Score a candidate word against the raw glide path.
     * Rewards: length match, prefix match, character subsequence coverage.
     */
    private fun score(candidate: String, glidePath: String): Double {
        if (glidePath.isEmpty()) return 0.0
        var score = 0.0
        if (candidate.startsWith(glidePath)) score += 5.0
        if (glidePath.startsWith(candidate.take(1))) score += 2.0
        // subsequence coverage: how many glide chars appear in candidate in order
        var ci = 0
        for (ch in glidePath) {
            val idx = candidate.indexOf(ch, ci)
            if (idx >= 0) { score += 1.0; ci = idx + 1 }
        }
        score -= kotlin.math.abs(candidate.length - glidePath.length) * 0.5
        return score
    }

    /** Top ranked words for the accumulated glide path. */
    fun decode(topK: Int = 3): List<String> {
        val raw = path.toString()
        if (raw.isEmpty()) return emptyList()
        return lexicon.asSequence()
            .map { it to score(it, raw) }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(topK)
            .map { it.first }
            .toList()
    }

    /** Best guess or fallback to the raw concatenated path (old behavior). */
    fun decodeBest(): String = decode(1).firstOrNull() ?: path.toString()
}
