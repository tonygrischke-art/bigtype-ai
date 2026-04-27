package com.aetheria.bigtype.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aetheria.bigtype.keyboard.ThemeMode

val DarkGlassColors = darkColorScheme(
    background = Color(0xFF0D0F1A),
    surface = Color(0xFF1E2235),
    primary = Color(0xFF00E5FF),
    secondary = Color(0xFF0097A7),
    onPrimary = Color(0xFFE8EAF6),
    onSecondary = Color(0xFF7986CB),
    error = Color(0xFFEF5350),
    tertiary = Color(0xFF69F0AE)
)

val NeonColors = darkColorScheme(
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF111111),
    primary = Color(0xFF00FF41),
    secondary = Color(0xFF00CC33),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFFCCCCCC),
    error = Color(0xFFFF0055),
    tertiary = Color(0xFFFFFF00)
)

val MinimalWhiteColors = darkColorScheme(
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF42A5F5),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF212121),
    error = Color(0xFFD32F2F),
    tertiary = Color(0xFF388E3C)
)

val AetheriaColors = darkColorScheme(
    background = Color(0xFF0D0A1A),
    surface = Color(0xFF1A1035),
    primary = Color(0xFFFFD54F),
    secondary = Color(0xFF9C27B0),
    onPrimary = Color(0xFF0D0A1A),
    onSecondary = Color(0xFFE8EAF6),
    error = Color(0xFFEF5350),
    tertiary = Color(0xFF7C4DFF)
)

@Composable
fun BigTypeTheme(
    themeMode: ThemeMode = ThemeMode.DARK_GLASS,
    content: @Composable () -> Unit
) {
    val colors = when (themeMode) {
        ThemeMode.DARK_GLASS -> DarkGlassColors
        ThemeMode.NEON -> NeonColors
        ThemeMode.MINIMAL_WHITE -> MinimalWhiteColors
        ThemeMode.AETHERIA -> AetheriaColors
    }
    MaterialTheme(
        colorScheme = colors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}