package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.aetheria.bigtype.keyboard.Snippet

@Composable
fun SnippetOverlay(
    snippets: List<Snippet>,
    onSelect: (Snippet) -> Unit,
    onDismiss: () -> Unit
) {
    Popup(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color(0xFF1E2235))
                .padding(8.dp)
        ) {
            Text("Code Snippets", color = Color(0xFF00E5FF), modifier = Modifier.padding(bottom = 8.dp))
            snippets.forEach { snippet ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(snippet) }
                        .padding(vertical = 4.dp)
                ) {
                    Text(snippet.trigger, color = Color(0xFFFFD54F), modifier = Modifier.weight(0.3f))
                    Text(snippet.title, color = Color(0xFFE8EAF6), modifier = Modifier.weight(0.7f))
                }
            }
        }
    }
}