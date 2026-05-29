package com.aetheria.bigtype.ime

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * BigType AI IME Service — minimal keyboard that works.
 *
 * Uses a lightweight Compose keyboard in onCreateInputView instead of the
 * full BigTypeKeyboardScreen (which needs ViewModel, Room, LLMClient, etc.).
 * This avoids the crash caused by heavy initialization during IME binding.
 */
class BigTypeIMEService : InputMethodService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("BigType", "IMEService.onCreate()")
        setCandidatesViewShown(false)
    }

    override fun onCreateInputView(): View {
        Log.d("BigType", "IMEService.onCreateInputView()")

        return try {
            ComposeView(this).apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnDetachedFromWindow
                )
                setContent {
                    MinimalKeyboard(
                        onKey = { label ->
                            handleKey(label)
                        }
                    )
                }
            }
        } catch (e: Throwable) {
            Log.e("BigType", "CRASH onCreateInputView: ${e.message}", e)
            // Last-resort fallback: plain Android View with a message
            android.widget.LinearLayout(this).apply {
                setBackgroundColor(0xFF0D0F1A.toInt())
                addView(android.widget.TextView(this@BigTypeIMEService).apply {
                    text = "BigType AI\nLoading error"
                    setTextColor(0xFFEF5350.toInt())
                    textSize = 14f
                    gravity = android.view.Gravity.CENTER
                })
            }
        }
    }

    private fun handleKey(label: String) {
        when (label) {
            "⌫" -> currentInputConnection?.deleteSurroundingText(1, 0)
            "⏎" -> currentInputConnection?.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
            )
            "SPACE" -> currentInputConnection?.commitText(" ", 1)
            else -> currentInputConnection?.commitText(label, 1)
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d("BigType", "onStartInput restarting=$restarting")
    }

    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        Log.d("BigType", "onStartInputView restarting=$restarting")
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        Log.d("BigType", "onFinishInputView")
    }

    override fun onDestroy() {
        Log.d("BigType", "IMEService.onDestroy()")
        super.onDestroy()
    }
}

@Composable
private fun MinimalKeyboard(onKey: (String) -> Unit) {
    val keyBg = Color(0xFF1E2235)
    val keyColor = Color(0xFFE8EAF6)
    val specialBg = Color(0xFF151826)

    val rows = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("z", "x", "c", "v", "b", "n", "m")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0F1A))
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Row 1: numbers hint row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..10).forEach { n ->
                Text(
                    text = "$n",
                    color = Color(0xFF7986CB),
                    fontSize = 12.sp,
                    modifier = Modifier.width(30.dp)
                )
            }
        }

        // QWERTY rows
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(keyBg)
                            .clickable { onKey(key) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = key, color = keyColor, fontSize = 16.sp)
                    }
                }
            }
        }

        // Bottom row: backspace + space + enter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(specialBg)
                    .clickable { onKey("⌫") },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⌫", color = Color(0xFFEF5350), fontSize = 20.sp)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(keyBg)
                    .clickable { onKey("SPACE") },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "space", color = Color(0xFF7986CB), fontSize = 13.sp)
            }
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(specialBg)
                    .clickable { onKey("⏎") },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⏎", color = Color(0xFF00E5FF), fontSize = 20.sp)
            }
        }
    }
}
