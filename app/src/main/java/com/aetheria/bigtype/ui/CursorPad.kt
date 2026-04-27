package com.aetheria.bigtype.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CursorPad(
    onCursorMove: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Button(onClick = { onCursorMove("UP") }, modifier = Modifier.size(60.dp)) {
                    Text("↑", color = Color(0xFFE8EAF6))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = { onCursorMove("LEFT") }, modifier = Modifier.size(60.dp)) {
                        Text("←", color = Color(0xFFE8EAF6))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onCursorMove("HOME") }, modifier = Modifier.size(60.dp)) {
                        Text("Home", color = Color(0xFFE8EAF6))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onCursorMove("RIGHT") }, modifier = Modifier.size(60.dp)) {
                        Text("→", color = Color(0xFFE8EAF6))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onCursorMove("DOWN") }, modifier = Modifier.size(60.dp)) {
                    Text("↓", color = Color(0xFFE8EAF6))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onCursorMove("END") }, modifier = Modifier.size(60.dp)) {
                    Text("End", color = Color(0xFFE8EAF6))
                }
            }
        }
    }
}
