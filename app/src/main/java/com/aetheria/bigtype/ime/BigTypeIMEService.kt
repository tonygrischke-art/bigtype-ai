package com.aetheria.bigtype.ime

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.aetheria.bigtype.keyboard.ThemeMode

/**
 * BigType AI IME Service — renders a visible, properly-sized keyboard.
 *
 * Fix history:
 * - v1: Heavy Compose + ViewModel crashed in onCreateInputView (IME binding timeout)
 * - v2: Minimal Compose keyboard was invisible (dark block on high-density screen)
 * - v3: Plain Android Views with proper dp-based sizing and themed colors
 */
class BigTypeIMEService : InputMethodService() {

    private var keyboardView: View? = null
    private var currentTheme = ThemeMode.DARK_GLASS

    override fun onCreate() {
        super.onCreate()
        Log.d("BigType", "IMEService.onCreate()")
        setCandidatesViewShown(false)
    }

    override fun onCreateInputView(): View {
        Log.d("BigType", "IMEService.onCreateInputView()")

        val container = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(currentTheme.bgColor())
        }

        try {
            keyboardView = buildKeyboard(container)
            container.addView(keyboardView)
            Log.d("BigType", "Keyboard view created successfully")
        } catch (e: Throwable) {
            Log.e("BigType", "Error building keyboard: ${e.message}", e)
        }

        return container
    }

    private fun buildKeyboard(container: FrameLayout): View {
        return LinearLayout(container.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val rows = listOf(
                listOf("q","w","e","r","t","y","u","i","o","p"),
                listOf("a","s","d","f","g","h","j","k","l"),
                listOf("z","x","c","v","b","n","m")
            )

            val keyTextColor = currentTheme.keyColor()
            val keyBgColor = currentTheme.keyBgColor()
            val specialBgColor = currentTheme.specialBgColor()
            val themeAccent = currentTheme.accentColor()

            // QWERTY rows
            rows.forEach { row ->
                addView(LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    row.forEach { key ->
                        addView(makeKey(key, keyTextColor, keyBgColor, dp(32)))
                    }
                })
            }

            // Bottom row: ⌫ SPACE ⏎
            addView(LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                addView(makeSpecialKey("⌫", 0xFFEF5350.toInt(), specialBgColor, dp(36), 0f) {
                    currentInputConnection?.deleteSurroundingText(1, 0)
                }, LinearLayout.LayoutParams(dp(40), dp(44), 0f).apply {
                    setMargins(0, dp(2), dp(2), 0)
                })

                addView(makeSpecialKey("space", keyTextColor, keyBgColor, dp(36), 1f) {
                    currentInputConnection?.commitText(" ", 1)
                }, LinearLayout.LayoutParams(0, dp(44), 1f).apply {
                    setMargins(dp(2), dp(2), dp(2), 0)
                })

                addView(makeSpecialKey("⏎", themeAccent, specialBgColor, dp(36), 0f) {
                    currentInputConnection?.sendKeyEvent(
                        KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
                    )
                    currentInputConnection?.sendKeyEvent(
                        KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)
                    )
                }, LinearLayout.LayoutParams(dp(40), dp(44), 0f).apply {
                    setMargins(dp(2), dp(2), 0, 0)
                })
            })
        }
    }

    private fun makeKey(label: String, textColor: Int, bgColor: Int, heightPx: Int): TextView {
        return TextView(this).apply {
            text = label
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            gravity = Gravity.CENTER
            setBackgroundColor(bgColor)
            layoutParams = LinearLayout.LayoutParams(0, heightPx, 1f).apply {
                setMargins(dp(2), dp(2), dp(2), dp(2))
            }
            setOnClickListener {
                currentInputConnection?.commitText(label, 1)
            }
        }
    }

    private fun makeSpecialKey(
        label: String,
        textColor: Int,
        bgColor: Int,
        heightPx: Int,
        weight: Float,
        action: () -> Unit
    ): TextView {
        return TextView(this).apply {
            text = label
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, if (label.length > 2) 14f else 16f)
            gravity = Gravity.CENTER
            setBackgroundColor(bgColor)
            setOnClickListener { action() }
        }
    }

    private fun dp(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d("BigType", "onStartInput restarting=$restarting")
    }

    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        Log.d("BigType", "onStartInputView restarting=$restarting")
        keyboardView?.setBackgroundColor(currentTheme.bgColor())
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        Log.d("BigType", "onFinishInputView")
    }

    override fun onDestroy() {
        Log.d("BigType", "IMEService.onDestroy()")
        keyboardView = null
        super.onDestroy()
    }
}

// Theme color helpers
private fun ThemeMode.bgColor(): Int = when (this) {
    ThemeMode.DARK_GLASS -> 0xFF0D0F1A.toInt()
    ThemeMode.NEON -> 0xFF0A0A0A.toInt()
    ThemeMode.MINIMAL_WHITE -> 0xFFF5F5F5.toInt()
    ThemeMode.AETHERIA -> 0xFF0D0A1A.toInt()
}

private fun ThemeMode.keyColor(): Int = when (this) {
    ThemeMode.MINIMAL_WHITE -> 0xFF212121.toInt()
    ThemeMode.NEON -> 0xFF00FF41.toInt()
    else -> 0xFFE8EAF6.toInt()
}

private fun ThemeMode.keyBgColor(): Int = when (this) {
    ThemeMode.DARK_GLASS -> 0xFF1E2235.toInt()
    ThemeMode.NEON -> 0xFF111111.toInt()
    ThemeMode.MINIMAL_WHITE -> 0xFFFFFFFF.toInt()
    ThemeMode.AETHERIA -> 0xFF1A1035.toInt()
}

private fun ThemeMode.specialBgColor(): Int = when (this) {
    ThemeMode.DARK_GLASS -> 0xFF151826.toInt()
    ThemeMode.NEON -> 0xFF0A1929.toInt()
    ThemeMode.MINIMAL_WHITE -> 0xFFE0E0E0.toInt()
    ThemeMode.AETHERIA -> 0xFF2D1F4E.toInt()
}

private fun ThemeMode.accentColor(): Int = when (this) {
    ThemeMode.DARK_GLASS -> 0xFF00E5FF.toInt()
    ThemeMode.NEON -> 0xFF00FF41.toInt()
    ThemeMode.MINIMAL_WHITE -> 0xFF1976D2.toInt()
    ThemeMode.AETHERIA -> 0xFFFFD54F.toInt()
}
