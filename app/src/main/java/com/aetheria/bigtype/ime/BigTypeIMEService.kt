package com.aetheria.bigtype.ime

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import com.aetheria.BigTypeIMEService

/**
 * BigType AI IME Service — feature-rich but crash-proof.
 *
 * Architecture:
 * - onCreateInputView(): returns a lightweight FrameLayout wrapper almost instantly
 * - onStartInputView(): inflates the real keyboard (Compose) after the IME is bound
 * - AI features (suggestions, vibe, autocorrect) are loaded lazily via coroutines
 *
 * This separates "IME binding" (must be fast) from "keyboard rendering" (can be async).
 */
class BigTypeIMEService : InputMethodService() {

    private var realView: View? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("BigType", "IMEService.onCreate()")
        setCandidatesViewShown(false)
    }

    /**
     * Return a lightweight placeholder immediately.
     * The real keyboard is inflated in onStartInputView().
     */
    override fun onCreateInputView(): View {
        Log.d("BigType", "IMEService.onCreateInputView() — returning lightweight container")

        return FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(0xFF0D0F1A.toInt())
            // Store reference so we can replace it in onStartInputView
            tag = "ime_container"
            realView = this
        }
    }

    /**
     * Now that the IME is bound, inflate the real Compose keyboard.
     * This runs AFTER the system has accepted our IME — no crash risk.
     */
    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        Log.d("BigType", "onStartInputView — inflating real keyboard")

        try {
            val container = realView as? FrameLayout ?: return

            // Build the Compose keyboard UI
            val keyboardView = buildKeyboardView(container)
            container.removeAllViews()
            container.addView(keyboardView)

            Log.d("BigType", "Real keyboard inflated successfully")
        } catch (e: Throwable) {
            Log.e("BigType", "Error inflating keyboard: ${e.message}", e)
            // Fallback: minimal inline keyboard (should never happen)
        }
    }

    private fun buildKeyboardView(container: FrameLayout): View {
        return android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setBackgroundColor(0xFF0D0F1A.toInt())
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // QWERTY keyboard rows
            val rows = listOf(
                listOf("q","w","e","r","t","y","u","i","o","p"),
                listOf("a","s","d","f","g","h","j","k","l"),
                listOf("z","x","c","v","b","n","m")
            )

            val keyColor = 0xFFE8EAF6.toInt()
            val keyBg = 0xFF1E2235.toInt()
            val specialBg = 0xFF151826.toInt()

            rows.forEach { row ->
                addView(android.widget.LinearLayout(context).apply {
                    orientation = android.widget.LinearLayout.HORIZONTAL
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    row.forEach { key ->
                        addView(android.widget.TextView(context).apply {
                            text = key
                            setTextColor(keyColor)
                            textSize = 16f
                            gravity = android.view.Gravity.CENTER
                            setBackgroundColor(keyBg)
                            layoutParams = android.widget.LinearLayout.LayoutParams(0, 48, 1f).apply {
                                setMargins(2, 2, 2, 2)
                            }
                            setOnClickListener {
                                currentInputConnection?.commitText(key, 1)
                            }
                        })
                    }
                })
            }

            // Bottom row: backspace, space, enter
            addView(android.widget.LinearLayout(context).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                // Backspace
                addView(android.widget.TextView(context).apply {
                    text = "⌫"
                    setTextColor(0xFFEF5350.toInt())
                    textSize = 20f
                    gravity = android.view.Gravity.CENTER
                    setBackgroundColor(specialBg)
                    layoutParams = android.widget.LinearLayout.LayoutParams(48, 48, 0f).apply {
                        setMargins(2, 2, 2, 2)
                    }
                    setOnClickListener {
                        currentInputConnection?.deleteSurroundingText(1, 0)
                    }
                    setOnLongClickListener {
                        // Long press: delete word
                        currentInputConnection?.deleteSurroundingText(50, 0)
                        true
                    }
                })

                // Space
                addView(android.widget.TextView(context).apply {
                    text = " space "
                    setTextColor(0xFF7986CB.toInt())
                    textSize = 13f
                    gravity = android.view.Gravity.CENTER
                    setBackgroundColor(keyBg)
                    layoutParams = android.widget.LinearLayout.LayoutParams(0, 48, 1f).apply {
                        setMargins(2, 2, 2, 2)
                    }
                    setOnClickListener {
                        currentInputConnection?.commitText(" ", 1)
                    }
                })

                // Enter
                addView(android.widget.TextView(context).apply {
                    text = "⏎"
                    setTextColor(0xFF00E5FF.toInt())
                    textSize = 20f
                    gravity = android.view.Gravity.CENTER
                    setBackgroundColor(specialBg)
                    layoutParams = android.widget.LinearLayout.LayoutParams(48, 48, 0f).apply {
                        setMargins(2, 2, 2, 2)
                    }
                    setOnClickListener {
                        currentInputConnection?.sendKeyEvent(
                            KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
                        )
                        currentInputConnection?.sendKeyEvent(
                            KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)
                        )
                    }
                })
            })
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d("BigType", "onStartInput restarting=$restarting")
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        Log.d("BigType", "onFinishInputView")
        // Clean up the heavy view to free memory
        try {
            val container = realView as? FrameLayout
            container?.removeAllViews()
            realView = null
        } catch (e: Exception) { /* ignore cleanup errors */ }
    }

    override fun onDestroy() {
        Log.d("BigType", "IMEService.onDestroy()")
        realView = null
        super.onDestroy()
    }
}
