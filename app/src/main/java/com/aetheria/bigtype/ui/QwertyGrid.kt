package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheria.bigtype.keyboard.KeyDef
import com.aetheria.bigtype.keyboard.KeyboardViewModel
import com.aetheria.bigtype.keyboard.QwertyRows

@Composable
fun QwertyGrid(
    viewModel: KeyboardViewModel,
    onTextInput: (String) -> Unit,
    keys: List<List<KeyDef>> = QwertyRows
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF0D0F1A)).padding(vertical = 4.dp)) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { keyDef ->
                    FatKey(
                        keyDef = keyDef,
                        modifier = Modifier.weight(1f),
                        onTap = {
                            when (keyDef.label) {
                                "⌫" -> { /* handled by parent */ }
                                "⇧" -> { /* toggle shift */ }
                                else -> onTextInput(keyDef.value)
                            }
                        },
                        onLongPress = {
                            keyDef.longPressValue?.let { onTextInput(it) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FatKey(
    keyDef: KeyDef,
    modifier: Modifier = Modifier,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (keyDef.isSpecial) Color(0xFF151826) else Color(0xFF1E2235))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLongPress() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = keyDef.label,
            color = Color(0xFFE8EAF6),
            fontSize = if (keyDef.isSpecial) 18.sp else 16.sp
        )
        if (keyDef.longPressValue != null) {
            Text(
                text = keyDef.longPressValue,
                color = Color(0xFF7986CB),
                fontSize = 9.sp,
                modifier = Modifier.align(Alignment.TopEnd).padding(2.dp)
            )
        }
    }
}