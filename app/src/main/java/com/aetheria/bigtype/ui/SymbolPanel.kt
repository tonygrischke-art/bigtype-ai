package com.aetheria.bigtype.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun SymbolPanel(
    onSymbolSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val symbolRows = listOf(
        listOf("{", "}", "[", "]", "(", ")", "<", ">"),
        listOf(";", ":", "'", "\"", "`", "~"),
        listOf("!", "@", "#", "$", "%", "^", "&", "*"),
        listOf("\\", "/", "|", "_", "-", "+", "=", "?")
    )

    Popup(onDismissRequest = onDismiss) {
        Surface(color = Color(0xFF1E2235), modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(8.dp)) {
                symbolRows.forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        row.forEach { symbol ->
                            Text(
                                text = symbol,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .clickable {
                                        onSymbolSelected(symbol)
                                        onDismiss()
                                    },
                                color = Color(0xFFE8EAF6)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}