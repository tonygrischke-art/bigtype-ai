package com.aetheria.bigtype.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MagnifierBubble(
    keyLabel: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = keyLabel,
            fontSize = 24.sp,
            color = Color(0xFF00E5FF)
        )
    }
}
