package com.aetheria.bigtype.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aetheria.bigtype.keyboard.KeyboardViewModel

@Composable
fun EmojiPanel(
    viewModel: KeyboardViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    Column(modifier = modifier.padding(8.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search 🚀", color = Color(0xFF7986CB)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text("Recent", color = Color(0xFF7986CB))
        LazyRow {
            items(state.recentEmojis) { emoji ->
                Text(
                    text = emoji,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { viewModel.onEmojiSelected(emoji) }
                )
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Text("Predicted", color = Color(0xFF7986CB))
        LazyRow {
            items(state.predictedEmojis) { emoji ->
                Text(
                    text = emoji,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { viewModel.onEmojiSelected(emoji) }
                )
            }
        }
    }
}
