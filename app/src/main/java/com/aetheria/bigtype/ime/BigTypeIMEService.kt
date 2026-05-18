package com.aetheria.bigtype.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.aetheria.bigtype.ui.BigTypeKeyboardScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BigTypeIMEService : InputMethodService() {

    @Inject
    lateinit var modifierStateManager: com.aetheria.bigtype.keyboard.ModifierStateManager

    private var keyboardView: ComposeKeyboardView? = null

    override fun onCreate() {
        super.onCreate()
        setCandidatesViewShown(false)
    }

    override fun onCreateInputView(): View {
        // Build the view once and cache it. The view initialises its lifecycle
        // to CREATED in its init block.
        return ComposeKeyboardView(this) {
            BigTypeKeyboardScreen(
                onTextInput = { text ->
                    val ic = currentInputConnection
                    if (ic != null) {
                        ic.commitText(text, 1)
                    }
                },
                onDelete = {
                    val ic = currentInputConnection
                    if (ic != null) {
                        ic.deleteSurroundingText(1, 0)
                    }
                },
                onKeyEvent = { keyCode ->
                    val ic = currentInputConnection
                    if (ic != null) {
                        val keyDownEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
                        ic.sendKeyEvent(keyDownEvent)
                        val keyUpEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
                        ic.sendKeyEvent(keyUpEvent)
                    }
                }
            )
        }.also { view ->
            keyboardView = view
        }
    }

    override fun onStartInputView(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)
        // Move lifecycle to RESUMED so ViewModel and LiveData access is safe.
        keyboardView?.onResume()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        // Take the lifecycle down to STOPPED so observers are cleared.
        keyboardView?.onStop()
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
    }

    override fun onDestroy() {
        keyboardView?.onDestroy()
        keyboardView = null
        super.onDestroy()
    }
}