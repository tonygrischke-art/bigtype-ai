package com.aetheria.bigtype.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aetheria.bigtype.keyboard.KeyboardViewModel
import com.aetheria.bigtype.keyboard.ModifierType

@Composable
fun DevKeyRow(
    viewModel: KeyboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val modifierState = viewModel.state.value.modifierState
    val devKeys = listOf(
        DevKey("Ctrl", ModifierType.CTRL),
        DevKey("Alt", ModifierType.ALT),
        DevKey("Shift", ModifierType.SHIFT),
        DevKey("Tab", null),
        DevKey("Esc", null),
        DevKey("↑", null),
        DevKey("↓", null),
        DevKey("←", null),
        DevKey("→", null),
        DevKey("Home", null),
        DevKey("End", null),
        DevKey("PgUp", null),
        DevKey("PgDn", null)
    )

    Row(modifier = modifier) {
        devKeys.forEach { key ->
            val isActive = key.modifierType != null && modifierState.activeModifiers.contains(key.modifierType)
            Button(
                onClick = { viewModel.onDevKeyPressed(key) },
                modifier = Modifier.weight(1f),
                colors = if (isActive)
                    androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                else
                    androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF151826))
            ) {
                Text(key.label, color = Color(0xFFE8EAF6))
            }
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

data class DevKey(
    val label: String,
    val modifierType: ModifierType?
)
