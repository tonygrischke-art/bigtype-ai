package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheria.bigtype.keyboard.KeyboardViewModel
import com.aetheria.bigtype.keyboard.VibeMode

@Composable
fun VibeSelector(viewModel: KeyboardViewModel) {
    val state by viewModel.state.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF12152A)).padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        VibeMode.values().forEach { vibe ->
            val isSelected = state.vibe == vibe
            val color = when (vibe) {
                VibeMode.PROFESSIONAL -> Color(0xFF0097A7)
                VibeMode.CASUAL -> Color(0xFF69F0AE)
                VibeMode.SNARKY -> Color(0xFF7986CB)
                VibeMode.ROAST -> Color(0xFFFF7043)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) color.copy(alpha = 0.2f) else Color(0xFF1E2235))
                    .border(if (isSelected) 1.dp else 0.dp, color, RoundedCornerShape(8.dp))
                    .clickable { viewModel.setVibe(vibe) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${vibe.emoji} ${vibe.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    color = if (isSelected) color else Color(0xFF7986CB),
                    fontSize = 11.sp
                )
            }
        }
    }
}