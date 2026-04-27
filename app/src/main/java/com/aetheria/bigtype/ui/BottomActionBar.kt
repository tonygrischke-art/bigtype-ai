package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheria.bigtype.keyboard.KeyboardViewModel

@Composable
fun BottomActionBar(
    viewModel: KeyboardViewModel,
    onTextInput: (String) -> Unit,
    onKeyEvent: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFF0D0F1A)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vibe button
        Text(
            text = state.vibe.emoji,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clickable { viewModel.cycleVibe() }
        )

        // Symbol key
        Text(
            text = "?123",
            color = Color(0xFF7986CB),
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 8.dp).clickable { /* open symbol panel */ }
        )

        // Spacebar - long press = rewrite
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .padding(horizontal = 4.dp)
                .background(Color(0xFF1E2235))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onTextInput(" ") },
                        onLongPress = { viewModel.rewriteSelectedText(state.currentText) }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (state.isPrivacyMode) {
                Text("🔒", fontSize = 16.sp)
            } else {
                Text(
                    text = state.vibe.emoji,
                    fontSize = 14.sp
                )
            }
        }

        // Backspace
        Text(
            text = "⌫",
            color = Color(0xFFE8EAF6),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable { onKeyEvent(67) } // KEYCODE_DEL
        )

        // Enter
        Text(
            text = "↵",
            color = Color(0xFF00E5FF),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clickable { onKeyEvent(66) } // KEYCODE_ENTER
        )
    }
}

