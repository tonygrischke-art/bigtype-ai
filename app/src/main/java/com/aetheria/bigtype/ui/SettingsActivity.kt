package com.aetheria.bigtype.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheria.bigtype.keyboard.ThemeMode
import com.aetheria.bigtype.ui.theme.BigTypeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BigTypeTheme {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    var selectedTheme by remember { mutableStateOf(ThemeMode.DARK_GLASS) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0D0F1A)).padding(16.dp)
    ) {
        Text("BigType AI Settings", color = Color(0xFF00E5FF), fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))

        Text("Theme", color = Color(0xFF7986CB), fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))

        ThemeMode.values().forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedTheme = theme }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = if (selectedTheme == theme) "● " else "○ ",
                    color = Color(0xFF00E5FF),
                    fontSize = 16.sp
                )
                Text(
                    text = theme.name.replace("_", " "),
                    color = if (selectedTheme == theme) Color(0xFFE8EAF6) else Color(0xFF7986CB),
                    fontSize = 15.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Version 1.0.0", color = Color(0xFF7986CB), fontSize = 12.sp)
        Text("BigType AI — Intelligent Android Keyboard", color = Color(0xFF7986CB), fontSize = 12.sp)
    }
}