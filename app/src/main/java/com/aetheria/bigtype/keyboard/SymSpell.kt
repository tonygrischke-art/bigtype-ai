package com.aetheria.bigtype.keyboard

/**
 * SymSpell spelling corrector - cannibalized from SymSpell (wolfbe6/SymSpell, MIT)
 * (symmetric-delete algorithm port for BigType).
 *
 * Core idea: precompute deletion variants of dictionary words once; lookups then
 * only need edit-distance-1 deletes of the input - O(1)-ish per query instead of
 * O(n) fuzzy matching. Huge upgrade over hand-maintained typo maps.
 */
class SymSpell(
    dictionary: Collection<String>,
    private val maxEditDistance: Int = 1
) {
    // word -> frequency (higher = better suggestion)
    private val wordFreq = HashMap<String, Int>()
    // deletion variant -> original words
    private val deletes = HashMap<String, ArrayList<String>>()

    data class Suggestion(val word: String, val distance: Int, val frequency: Int)

    init {
        for (word in dictionary) addWord(word, 1)
    }

    fun addWord(word: String, frequency: Int = 1) {
        val key = word.lowercase()
        val newFreq = (wordFreq[key] ?: 0) + frequency
        wordFreq[key] = newFreq
        if (key.length <= maxEditDistance) return
        for (variant in deletesOf(key)) {
            deletes.getOrPut(variant) { ArrayList() }.add(key)
        }
    }

    private fun deletesOf(word: String): List<String> {
        val out = ArrayList<String>()
        var current = listOf(word)
        repeat(maxEditDistance) {
            val next = ArrayList<String>()
            for (w in current) {
                for (i in w.indices) {
                    val d = w.removeRange(i, i + 1)
                    out.add(d)
                    next.add(d)
                }
            }
            current = next
        }
        return out
    }

    private fun editDistance1Bounded(a: String, b: String): Int {
        if (kotlin.math.abs(a.length - b.length) > maxEditDistance) return -1
        var i = 0; var j = 0; var edits = 0
        while (i < a.length && j < b.length) {
            if (a[i] != b[j]) {
                if (++edits > maxEditDistance) return -1
                when {
                    a.length == b.length -> { i++; j++ }          // substitution
                    a.length > b.length -> i++                    // deletion from a
                    else -> j++                                   // insertion in a
                }
            } else { i++; j++ }
        }
        if (i < a.length || j < b.length) edits++
        return if (edits <= maxEditDistance) edits else -1
    }

    /** Best spelling suggestions for [input], ordered by distance then frequency. */
    fun lookup(input: String, topK: Int = 3): List<Suggestion> {
        val key = input.lowercase()
        val direct = wordFreq[key]
        if (direct != null) return listOf(Suggestion(key, 0, direct))

        val candidates = HashSet<String>()
        candidates.add(key)
        deletesOf(key).forEach { d ->
            deletes[d]?.let { originals -> candidates.addAll(originals) }
        }
        deletes[key]?.let { candidates.addAll(it) }

        return candidates.mapNotNull { cand ->
            val dist = editDistance1Bounded(key, cand)
            if (dist in 0..maxEditDistance)
                Suggestion(cand, dist, wordFreq[cand] ?: 0)
            else null
        }.sortedWith(compareBy({ it.distance }, { -it.frequency }))
            .take(topK)
    }
}

/** Compact English core dictionary (top ~300 common words). Extend from Room. */
object CoreEnglishLexicon {
    val WORDS = listOf(
        "the","be","to","of","and","a","in","that","have","i","it","for","not","on","with",
        "he","as","you","do","at","this","but","his","by","from","they","we","say","her","she",
        "or","an","will","my","one","all","would","there","their","what","so","up","out","if",
        "about","who","get","which","go","me","when","make","can","like","time","no","just",
        "him","know","take","people","into","year","your","good","some","could","them","see",
        "other","than","then","now","look","only","come","its","over","think","also","back",
        "after","use","two","how","our","work","first","well","way","even","new","want",
        "because","any","these","give","day","most","us","is","are","was","were","been","has",
        "had","did","said","each","more","long","here","very","didn't","don't","can't","won't",
        "hello","hi","hey","thanks","thank","please","yes","okay","ok","love","great","good",
        "morning","night","today","tomorrow","yesterday","soon","later","right","left",
        "home","phone","text","message","send","call","email","meet","meeting","done",
        "really","probably","maybe","never","always","sometimes","definitely","actually",
        "fire","lol","lmao","omg","nice","cool","awesome","sorry","fine","sure","nope"
    )
}
