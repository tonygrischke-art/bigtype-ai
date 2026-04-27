package com.aetheria.bigtype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.aetheria.bigtype.clipboard.ClipboardManager
import com.aetheria.bigtype.keyboard.KeyboardViewModel
import com.aetheria.bigtype.ui.theme.BigTypeTheme

@Composable
fun BigTypeKeyboardScreen(
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onKeyEvent: (Int) -> Unit
) {
    val viewModel: KeyboardViewModel = hiltViewModel()
    val clipboardViewModel: ClipboardManager = hiltViewModel()
    val state by viewModel.state.collectAsState()

    BigTypeTheme(themeMode = state.theme) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D0F1A))
        ) {
            ContextPowerBar(viewModel = viewModel, onResult = { onTextInput(it) })
            SuggestionStrip(viewModel = viewModel, onInsert = { onTextInput(it) })

            if (state.showNumberRow) {
                QwertyGrid(
                    viewModel = viewModel,
                    onTextInput = onTextInput,
                    keys = listOf(com.aetheria.bigtype.keyboard.NumberRow)
                )
            }

            if (state.isHexLayout) {
                HexGrid(viewModel = viewModel, onTextInput = onTextInput)
            } else {
                QwertyGrid(viewModel = viewModel, onTextInput = onTextInput)
            }

            if (state.isTerminalOpen) {
                TerminalRow(viewModel = viewModel, onTextInput = onTextInput)
            }

            BottomActionBar(
                viewModel = viewModel,
                onTextInput = onTextInput,
                onKeyEvent = onKeyEvent
            )
        }
    }
}

