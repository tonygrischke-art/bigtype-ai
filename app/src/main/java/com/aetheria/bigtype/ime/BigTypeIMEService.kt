package com.aetheria.bigtype.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.KeyEvent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
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
import com.aetheria.bigtype.keyboard.KeyboardViewModel
import com.aetheria.bigtype.llm.LLMClient
import com.aetheria.bigtype.bridge.BridgeClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BigTypeIMEService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    @Inject
    lateinit var modifierStateManager: com.aetheria.bigtype.keyboard.ModifierStateManager

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        setCandidatesViewShown(false)
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        // Create ViewModel manually — hiltViewModel() doesn't work in IME service
        val viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return KeyboardViewModel(
                        llmClient = LLMClient(),
                        bridgeClient = BridgeClient(),
                        modifierManager = modifierStateManager,
                    ) as T
                }
            }
        ).get(KeyboardViewModel::class.java)

        return ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setViewTreeLifecycleOwner(this@BigTypeIMEService)
            setViewTreeSavedStateRegistryOwner(this@BigTypeIMEService)
            setViewTreeViewModelStoreOwner(this@BigTypeIMEService)
            setContent {
                BigTypeKeyboardScreen(
                    viewModel = viewModel,
                    onTextInput = { text ->
                        val ic = currentInputConnection
                        if (ic != null) { ic.commitText(text, 1) }
                    },
                    onDelete = {
                        val ic = currentInputConnection
                        if (ic != null) { ic.deleteSurroundingText(1, 0) }
                    },
                    onKeyEvent = { keyCode ->
                        val ic = currentInputConnection
                        if (ic != null) {
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
                        }
                    }
                )
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        super.onDestroy()
    }
}