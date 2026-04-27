package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheria.bigtype.keyboard.KeyboardViewModel

data class TerminalChip(val label: String, val command: String, val isGit: Boolean = false)

val terminalChips = listOf(
    TerminalChip("git status", "git status", isGit = true),
    TerminalChip("git add .", "git add .", isGit = true),
    TerminalChip("git push", "git push origin HEAD", isGit = true),
    TerminalChip("git pull", "git pull", isGit = true),
    TerminalChip("git diff", "git diff", isGit = true),
    TerminalChip("ls", "ls"),
    TerminalChip("cd ..", "cd .."),
    TerminalChip("python3", "python3"),
    TerminalChip("llama-server", "llama-server --port 8080")
)

@Composable
fun TerminalRow(viewModel: KeyboardViewModel, onTextInput: (String) -> Unit) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0C12))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$ ", color = Color(0xFF00FF41), fontSize = 12.sp)
            Text(
                text = if (state.gitBranch.isNotEmpty()) "branch: ${state.gitBranch}" else "terminal",
                color = Color(0xFF00FF41),
                fontSize = 11.sp
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "✕",
                color = Color(0xFF7986CB),
                fontSize = 14.sp,
                modifier = Modifier.clickable { viewModel.toggleTerminal() }
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(terminalChips) { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (chip.isGit) Color(0xFF0D1F0D) else Color(0xFF111325))
                        .clickable { onTextInput(chip.command) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = chip.label,
                        color = if (chip.isGit) Color(0xFF00FF41) else Color(0xFF7986CB),
                        fontSize = 11.sp
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1A1035))
                        .clickable { viewModel.generateCommitMessage() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("✨ commit msg", color = Color(0xFFFFD54F), fontSize = 11.sp)
                }
            }
        }
    }
}