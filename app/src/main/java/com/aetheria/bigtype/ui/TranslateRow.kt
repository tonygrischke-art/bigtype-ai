package com.aetheria.bigtype.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aetheria.bigtype.keyboard.KeyboardViewModel

@Composable
fun TranslateRow(
    viewModel: KeyboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.value
    if (state.isTranslateMode) {
        Row(modifier = modifier) {
            Text(
                text = "Translation: ${state.translation}",
                color = Color(0xFF00E5FF),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🌐",
                modifier = Modifier.clickable { viewModel.toggleTranslateMode() },
                color = Color(0xFFE8EAF6)
            )
        }
    }
}