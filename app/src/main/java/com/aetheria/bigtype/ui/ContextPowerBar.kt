package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*nimport androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

data class PowerAction(val label: String, val prompt: String)

val powerActions = listOf(
    PowerAction("Summarize", "Summarize this text concisely:"),
    PowerAction("Fix Code", "Fix any bugs in this code:"),
    PowerAction("Grammar Fix", "Fix grammar and spelling:"),
    PowerAction("Translate", "Translate to English:"),
    PowerAction("Ask Eve", "Answer this question:"),
    PowerAction("Shorten", "Make this shorter:"),
    PowerAction("Expand", "Expand this into more detail:")
)

@Composable
fun ContextPowerBar(viewModel: KeyboardViewModel, onResult: (String) -> Unit) {
    val state by viewModel.state.collectAsState()
    if (state.isPrivacyMode) return

    LazyRow(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF12152A)).padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(powerActions) { action ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E2235))
                    .clickable {
                        val text = state.currentText
                        if (text.isNotEmpty()) {
                            viewModel.rewriteSelectedText(text)
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(action.label, color = Color(0xFF7986CB), fontSize = 12.sp)
            }
        }
    }
}

