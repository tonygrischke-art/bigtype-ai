package com.aetheria.bigtype.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.aetheria.bigtype.ui.BigTypeKeyboardScreen
import com.aetheria.bigtype.ui.theme.BigTypeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BigTypeIMEService : InputMethodService(),
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val _viewModelStore = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = _viewModelStore

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override val lifecycle: Lifecycle = object : Lifecycle() {
        private var _currentState = State.INITIALIZED
        override val currentState: State get() = _currentState
        
        fun updateState(state: State) {
            _currentState = state
        }

        override fun addObserver(observer: LifecycleObserver) {
            // no-op for IME
        }

        override fun removeObserver(observer: LifecycleObserver) {
            // no-op for IME
        }
    }

    override fun onCreate() {
        savedStateRegistryController.performRestore(null)
        super.onCreate()
    }

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setViewTreeLifecycleOwner(this@BigTypeIMEService)
            setViewTreeViewModelStoreOwner(this@BigTypeIMEService)
            setViewTreeSavedStateRegistryOwner(this@BigTypeIMEService)
            setContent {
                BigTypeTheme {
                    BigTypeKeyboardScreen(
                        onTextInput = { text ->
                            currentInputConnection?.commitText(text, 1)
                        },
                        onDelete = {
                            currentInputConnection?.deleteSurroundingText(1, 0)
                        },
                        onKeyEvent = { keyCode ->
                            val down = KeyEvent(System.currentTimeMillis(), System.currentTimeMillis(), KeyEvent.ACTION_DOWN, keyCode, 0)
                            val up = KeyEvent(System.currentTimeMillis(), System.currentTimeMillis(), KeyEvent.ACTION_UP, keyCode, 0)
                            currentInputConnection?.sendKeyEvent(down)
                            currentInputConnection?.sendKeyEvent(up)
                        }
                    )
                }
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModelStore.clear()
    }
}