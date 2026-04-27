package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aetheria.bigtype.keyboard.HexRows
import com.aetheria.bigtype.keyboard.KeyboardViewModel

@Composable
fun HexGrid(viewModel: KeyboardViewModel, onTextInput: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF0D0F1A)).padding(vertical = 4.dp)) {
        HexRows.forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = if (index % 2 == 1) 24.dp else 0.dp, top = 2.dp, bottom = 2.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { keyDef ->
                    FatKey(
                        keyDef = keyDef,
                        modifier = Modifier.weight(1f),
                        onTap = { onTextInput(keyDef.value) },
                        onLongPress = { keyDef.longPressValue?.let { onTextInput(it) } }
                    )
                }
            }
        }
    }
}