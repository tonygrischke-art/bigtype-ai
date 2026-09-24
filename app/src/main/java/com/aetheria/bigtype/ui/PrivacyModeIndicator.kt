package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheria.bigtype.keyboard.PrivacyReason

/** Red banner shown across the keyboard whenever a secure field is focused. */
@Composable
fun PrivacyModeIndicator(reason: PrivacyReason) {
    if (reason == PrivacyReason.NONE) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFCDD2))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("\uD83D\uDD12 Private Mode", color = Color(0xFFB71C1C), fontSize = 12.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = when (reason) {
                PrivacyReason.SECURE_FIELD -> "Password field"
                PrivacyReason.BANKING_APP -> "Banking app"
                PrivacyReason.USER_DISABLED -> "Privacy mode on"
                PrivacyReason.NONE -> ""
            },
            fontSize = 10.sp,
            color = Color(0xFF7F0000)
        )
    }
}
