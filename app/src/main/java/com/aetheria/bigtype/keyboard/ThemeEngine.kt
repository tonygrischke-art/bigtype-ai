package com.aetheria.bigtype.keyboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class KeyboardTheme(val displayName: String, val baseColor: Long) {
    DARK_GLASS("Dark Glass", 0xFF0D0F1A),
    NEON("Neon", 0xFF0A1929),
    MINIMAL_WHITE("Minimal White", 0xFFFAFAFA),
    AETHERIA("Aetheria", 0xFF1A0A2E)
}

data class ThemeState(
    val currentTheme: KeyboardTheme = KeyboardTheme.DARK_GLASS,
    val wpm: Int = 0,
    val glowIntensity: Float = 0f,
    val isPulsing: Boolean = false
)

class ThemeEngine @Inject constructor() {
    private val _state = MutableStateFlow(ThemeState())
    val state: StateFlow<ThemeState> = _state
    
    private var keyPressTimes = mutableListOf<Long>()
    
    fun cycleTheme() {
        val themes = KeyboardTheme.entries
        val currentIndex = themes.indexOf(_state.value.currentTheme)
        val nextIndex = (currentIndex + 1) % themes.size
        _state.value = _state.value.copy(currentTheme = themes[nextIndex])
    }
    
    fun setTheme(theme: KeyboardTheme) {
        _state.value = _state.value.copy(currentTheme = theme)
    }
    
    fun onKeyPressed() {
        keyPressTimes.add(System.currentTimeMillis())
        
        val now = System.currentTimeMillis()
        keyPressTimes = keyPressTimes.filter { now - it < 5000 }.toMutableList()
        
        calculateWpmAndGlow()
    }
    
    private fun calculateWpmAndGlow() {
        if (keyPressTimes.size < 2) {
            _state.value = _state.value.copy(wpm = 0, glowIntensity = 0f, isPulsing = false)
            return
        }
        
        val recentPresses = keyPressTimes.takeLast(10)
        if (recentPresses.size < 2) return
        
        val totalTimeMs = recentPresses.last() - recentPresses.first()
        if (totalTimeMs <= 0) return
        
        val words = recentPresses.size / 5.0
        val minutes = totalTimeMs / 60000.0
        val wpm = (words / minutes).toInt().coerceIn(0, 200)
        
        val glowIntensity = when {
            wpm > 80 -> 1f
            wpm > 60 -> 0.75f
            wpm > 40 -> 0.5f
            wpm > 20 -> 0.25f
            else -> 0f
        }
        
        _state.value = _state.value.copy(
            wpm = wpm,
            glowIntensity = glowIntensity,
            isPulsing = wpm > 60
        )
    }
    
    fun getThemeColor(theme: KeyboardTheme): Long = theme.baseColor
    
    fun getGlowColor(theme: KeyboardTheme): Long {
        return when (theme) {
            KeyboardTheme.DARK_GLASS -> 0xFF00E5FF
            KeyboardTheme.NEON -> 0xFF00FF41
            KeyboardTheme.MINIMAL_WHITE -> 0xFF1976D2
            KeyboardTheme.AETHERIA -> 0xFFFFD700
        }
    }
}