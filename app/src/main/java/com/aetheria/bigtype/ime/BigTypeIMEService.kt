package com.aetheria.bigtype.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.aetheria.bigtype.ui.BigTypeKeyboardScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BigTypeIMEService : InputMethodService(),
    LifecycleOwner,
    SavedStateRegistryOwner,
    ViewModelStoreOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore = ViewModelStore()

    @Inject
    lateinit var modifierStateManager: com.aetheria.bigtype.keyboard.ModifierStateManager

    private var keyboardView: ComposeKeyboardView? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        setCandidatesViewShown(false)
    }

    override fun onStartInputView(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        keyboardView?.onResume()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        keyboardView?.onStop()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
    }

    override fun onDestroy() {
        keyboardView?.onDestroy()
        keyboardView = null
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        // Set lifecycle owners on the IME window decor view so that
        // ViewTreeLifecycleOwner is available for any Compose content
        // inflated within the IME hierarchy (fixes "ViewTreeLifecycleOwner
        // not found from LinearLayout" crash).
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
        }

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
}