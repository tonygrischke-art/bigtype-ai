package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.aetheria.bigtype.keyboard.VibeMode
import com.aetheria.bigtype.keyboard.ThemeMode
import com.aetheria.bigtype.keyboard.LLMStatus
import com.aetheria.bigtype.ui.theme.BigTypeTheme

@Composable
fun BigTypeKeyboardScreen(
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onKeyEvent: (Int) -> Unit
) {
    var currentText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var vibe by remember { mutableStateOf(VibeMode.CASUAL) }
    var theme by remember { mutableStateOf(ThemeMode.DARK_GLASS) }
    var showNumberRow by remember { mutableStateOf(false) }
    var isHexLayout by remember { mutableStateOf(false) }
    var isTerminalOpen by remember { mutableStateOf(false) }
    var smartReplies by remember { mutableStateOf<List<String>>(emptyList()) }
    var predictedEmojis by remember { mutableStateOf<List<String>>(emptyList()) }
    var rewriteResult by remember { mutableStateOf("") }

    BigTypeTheme(themeMode = theme) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D0F1A))
        ) {
            ContextPowerBarStandalone(
                currentText = currentText,
                onRewrite = { text ->
                    rewriteResult = "[AI: $text]"
                }
            )
            SuggestionStripStandalone(
                suggestions = suggestions,
                smartReplies = smartReplies,
                predictedEmojis = predictedEmojis,
                llmStatus = LLMStatus.OFFLINE,
                rewriteResult = rewriteResult,
                onInsert = { onTextInput(it) },
                onClearRewrite = { rewriteResult = "" }
            )

            if (showNumberRow) {
                QwertyGridStandalone(onTextInput = onTextInput, numberRowOnly = true)
            }

            if (isHexLayout) {
                HexGridStandalone(onTextInput = onTextInput)
            } else {
                QwertyGridStandalone(onTextInput = onTextInput)
            }

            if (isTerminalOpen) {
                TerminalRowStandalone(onTextInput = onTextInput)
            }

            BottomActionBarStandalone(
                vibe = vibe,
                onCycleVibe = {
                    val values = VibeMode.values()
                    vibe = values[(values.indexOf(vibe) + 1) % values.size]
                },
                onTextInput = onTextInput,
                onKeyEvent = onKeyEvent
            )
        }
    }
}

@Composable
fun ContextPowerBarStandalone(
    currentText: String,
    onRewrite: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF12152A))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf("Summarize", "Fix Code", "Grammar", "Translate").forEach { label ->
            Box(
                modifier = Modifier
                    .background(Color(0xFF1E2235))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(label, color = Color(0xFF7986CB), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun SuggestionStripStandalone(
    suggestions: List<String>,
    smartReplies: List<String>,
    predictedEmojis: List<String>,
    llmStatus: LLMStatus,
    rewriteResult: String,
    onInsert: (String) -> Unit,
    onClearRewrite: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0xFF161929)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (suggestions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (llmStatus == LLMStatus.ONLINE) "Start typing..." else "LLM offline",
                        color = Color(0xFF7986CB),
                        fontSize = 13.sp
                    )
                }
            } else {
                suggestions.forEachIndexed { index, suggestion ->
                    val isCenter = index == 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (isCenter) Color(0xFF1E2235) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            suggestion,
                            color = if (isCenter) Color(0xFF00E5FF) else Color(0xFFE8EAF6),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        if (rewriteResult.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A2030))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(rewriteResult, color = Color(0xFFE8EAF6), fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("✓", color = Color(0xFF69F0AE), fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                Text("✕", color = Color(0xFFEF5350), fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun QwertyGridStandalone(onTextInput: (String) -> Unit, numberRowOnly: Boolean = false) {
    val rows = if (numberRowOnly) {
        listOf(listOf("1","2","3","4","5","6","7","8","9","0"))
    } else {
        listOf(
            listOf("q","w","e","r","t","y","u","i","o","p"),
            listOf("a","s","d","f","g","h","j","k","l"),
            listOf("z","x","c","v","b","n","m")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0F1A))
            .padding(vertical = 4.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .padding(2.dp)
                            .background(Color(0xFF1E2235))
                            .clickable { onTextInput(key) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(key, color = Color(0xFFE8EAF6), fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun HexGridStandalone(onTextInput: (String) -> Unit) {
    val rows = listOf(
        listOf("q","w","e","r","t","y"),
        listOf("u","i","o","p","a","s"),
        listOf("d","f","g","h","j","k"),
        listOf("l","z","x","c","v","b"),
        listOf("n","m",",",".","?","!")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0F1A))
            .padding(vertical = 4.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .padding(2.dp)
                            .background(Color(0xFF1E2235))
                            .clickable { onTextInput(key) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(key, color = Color(0xFFE8EAF6), fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalRowStandalone(onTextInput: (String) -> Unit) {
    val chips = listOf("git status", "git add .", "ls", "python3")

    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0C12))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$ ", color = Color(0xFF00FF41), fontSize = 12.sp)
            Text("terminal", color = Color(0xFF00FF41), fontSize = 11.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            chips.forEach { chip ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFF111325))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clickable { onTextInput(chip) }
                ) {
                    Text(chip, color = Color(0xFF7986CB), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun BottomActionBarStandalone(
    vibe: VibeMode,
    onCycleVibe: () -> Unit,
    onTextInput: (String) -> Unit,
    onKeyEvent: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFF0D0F1A)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            vibe.emoji,
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 12.dp).clickable { onCycleVibe() }
        )

        Text("?123", color = Color(0xFF7986CB), fontSize = 13.sp, modifier = Modifier.padding(horizontal = 8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .padding(horizontal = 4.dp)
                .background(Color(0xFF1E2235))
                .clickable { onTextInput(" ") },
            contentAlignment = Alignment.Center
        ) {
            Text(vibe.emoji, fontSize = 14.sp)
        }

        Text("⌫", color = Color(0xFFE8EAF6), fontSize = 20.sp, modifier = Modifier.padding(horizontal = 8.dp).clickable { onKeyEvent(67) })
        Text("↵", color = Color(0xFF00E5FF), fontSize = 20.sp, modifier = Modifier.padding(horizontal = 12.dp).clickable { onKeyEvent(66) })
    }
}