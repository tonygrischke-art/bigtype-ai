package com.aetheria.bigtype.keyboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Glide decoder with word-level swipe decoding.
 * Raw path tracking preserved; onGlideEnd now returns the best dictionary word
 * (SwipeDecoder, FlorisBoard-inspired) instead of the raw key concat.
 */
class GlideDecoder(
    private val swipeDecoder: SwipeDecoder = SwipeDecoder()
) {
    private val _glidePath = MutableStateFlow<List<String>>(emptyList())
    val glidePath: StateFlow<List<String>> = _glidePath

    val _swipeSuggestions = MutableStateFlow<List<String>>(emptyList())
    val swipeSuggestions: StateFlow<List<String>> = _swipeSuggestions

    fun onGlideMove(keyLabel: String) {
        _glidePath.value = _glidePath.value + keyLabel
        swipeDecoder.onKey(keyLabel)
    }

    fun onGlideEnd(): String {
        val best = swipeDecoder.decodeBest()
        _swipeSuggestions.value = swipeDecoder.decode(3)
        _glidePath.value = emptyList()
        swipeDecoder.reset()
        return best
    }
}
