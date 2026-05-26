package com.aetheria.bigtype.keyboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow




class GlideDecoder constructor() {
    private val _glidePath = MutableStateFlow<List<String>>(emptyList())
    val glidePath: StateFlow<List<String>> = _glidePath

    fun onGlideMove(keyLabel: String) {
        _glidePath.value = _glidePath.value + keyLabel
    }

    fun onGlideEnd(): String {
        val path = _glidePath.value.joinToString("")
        _glidePath.value = emptyList()
        return path
    }
}