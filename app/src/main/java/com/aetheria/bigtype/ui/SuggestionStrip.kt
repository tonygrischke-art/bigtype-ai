package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheria.bigtype.keyboard.KeyboardViewModel

@Composable
fun SuggestionStrip(viewModel: KeyboardViewModel, onInsert: (String) -> Unit) {
    val state by viewModel.state.collectAsState()

    if (state.isPrivacyMode) { PrivacyIndicator(); return }

    Column {
        if (state.smartReplies.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0D0F1A)).padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(state.smartReplies) { reply ->
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFF1E2235))
                            .clickable { onInsert(reply) }.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text(reply, color = Color(0xFF00E5FF), fontSize = 12.sp) }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(44.dp).background(Color(0xFF161929)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.suggestions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (state.llmStatus.name == "ONLINE") "Start typing…" else "LLM offline", color = Color(0xFF7986CB), fontSize = 13.sp)
                }
            } else {
                state.suggestions.forEachIndexed { index, suggestion ->
                    val isCenter = index == 1
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                            .clickable { onInsert(suggestion) }
                            .background(if (isCenter) Color(0xFF1E2235) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(suggestion, color = if (isCenter) MaterialTheme.colorScheme.primary else Color(0xFFE8EAF6),
                            fontSize = 14.sp, fontWeight = if (isCenter) FontWeight.Bold else FontWeight.Normal, maxLines = 1)
                    }
                    if (index < state.suggestions.size - 1)
                        Box(Modifier.width(1.dp).height(24.dp).background(Color(0xFF2D3450)))
                }
            }
            state.predictedEmojis.take(3).forEach { emoji ->
                Text(emoji, modifier = Modifier.padding(horizontal = 6.dp).clickable { onInsert(emoji) }, fontSize = 18.sp)
            }
        }
        if (state.rewriteResult.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF1A2030)).padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(state.rewriteResult, color = Color(0xFFE8EAF6), fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("✓", color = Color(0xFF69F0AE), fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp).clickable { onInsert(state.rewriteResult); viewModel.clearRewriteResult() })
                Text("✕", color = Color(0xFFEF5350), fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp).clickable { viewModel.clearRewriteResult() })
            }
        }
    }
}

@Composable
fun PrivacyIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth().height(44.dp).background(Color(0xFF1A0000)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text("🔒 Privacy Mode — AI disabled", color = Color(0xFFEF5350), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}