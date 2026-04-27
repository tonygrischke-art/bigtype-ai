package com.aetheria.bigtype.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.aetheria.bigtype.keyboard.KeyboardViewModel

@Composable
fun OneHandedHandle(
    viewModel: KeyboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.value
    if (state.isOneHanded) {
        Box(modifier = modifier.fillMaxWidth()) {
            if (state.oneHandedSide == "left") {
                Row {
                    Box(modifier = Modifier.weight(3f))
                    Button(
                        onClick = { viewModel.toggleOneHandedSide() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("→", color = Color(0xFFE8EAF6))
                    }
                }
            } else {
                Row {
                    Button(
                        onClick = { viewModel.toggleOneHandedSide() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("←", color = Color(0xFFE8EAF6))
                    }
                    Box(modifier = Modifier.weight(3f))
                }
            }
        }
    }
}
