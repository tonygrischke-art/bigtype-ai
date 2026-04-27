package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheria.bigtype.clipboard.ClipboardManager
import com.aetheria.bigtype.clipboard.ClipType

@Composable
fun ClipboardPanel(viewModel: ClipboardManager, onInsert: (String) -> Unit) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFF111325))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Pinned", "Templates").forEachIndexed { index, tab ->
                Text(
                    text = tab,
                    color = if (state.selectedTab == index) Color(0xFF00E5FF) else Color(0xFF7986CB),
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { /* update tab */ }
                )
            }
        }

        val filtered = when (state.selectedTab) {
            1 -> state.clips.filter { it.isPinned }
            else -> state.clips
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filtered) { clip ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E2235))
                        .clickable { onInsert(clip.text) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val typeIcon = when (clip.type) {
                        ClipType.URL -> "🔗"
                        ClipType.CODE -> "💻"
                        ClipType.EMAIL -> "📧"
                        ClipType.PHONE -> "📱"
                        else -> "📋"
                    }
                    Text(typeIcon, fontSize = 14.sp, modifier = Modifier.padding(end = 6.dp))
                    Text(
                        text = clip.text,
                        color = Color(0xFFE8EAF6),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (clip.isPinned) Text("📌", fontSize = 12.sp)
                    Text(
                        text = "✕",
                        color = Color(0xFF7986CB),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 6.dp).clickable { viewModel.deleteClip(clip.id) }
                    )
                }
            }
        }
    }
}

