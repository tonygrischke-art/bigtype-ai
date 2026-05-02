package com.aetheria.bigtype.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aetheria.bigtype.keyboard.QwertyRows
import com.aetheria.bigtype.keyboard.KeyboardViewModel

@Composable
fun SplitKeyboard(
    viewModel: KeyboardViewModel = hiltViewModel(),
    onTextInput: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            QwertyRows[0].take(5).forEach { keyDef ->
                Button(
                    onClick = { onTextInput(keyDef.value) }
                ) {
                    Text(keyDef.label)
                }
            }
            QwertyRows[1].take(5).forEach { keyDef ->
                Button(
                    onClick = { onTextInput(keyDef.value) }
                ) {
                    Text(keyDef.label)
                }
            }
            QwertyRows[2].take(4).forEach { keyDef ->
                Button(
                    onClick = { onTextInput(keyDef.value) }
                ) {
                    Text(keyDef.label)
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            QwertyRows[0].drop(5).forEach { keyDef ->
                Button(
                    onClick = { onTextInput(keyDef.value) }
                ) {
                    Text(keyDef.label)
                }
            }
            QwertyRows[1].drop(5).forEach { keyDef ->
                Button(
                    onClick = { onTextInput(keyDef.value) }
                ) {
                    Text(keyDef.label)
                }
            }
            QwertyRows[2].drop(4).forEach { keyDef ->
                Button(
                    onClick = { onTextInput(keyDef.value) }
                ) {
                    Text(keyDef.label)
                }
            }
        }
    }
}
