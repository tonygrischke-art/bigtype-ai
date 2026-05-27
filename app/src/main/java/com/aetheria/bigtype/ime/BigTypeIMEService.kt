package com.aetheria.bigtype.ime

import android.inputmethodservice.InputMethodService
import android.util.Log
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
import com.aetheria.bigtype.BigTypeApp
import com.aetheria.bigtype.ui.BigTypeKeyboardScreen
import com.aetheria.bigtype.keyboard.KeyboardViewModel

class BigTypeIMEService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    override fun onCreate() {
        super.onCreate()
        Log.d("BigType", "IMEService.onCreate() called")
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        setCandidatesViewShown(false)
        Log.d("BigType", "IMEService.onCreate() finished")
    }

    override fun onCreateInputView(): View {
        Log.d("BigType", "IMEService.onCreateInputView() called")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        Log.d("BigType", "IMEService: creating ComposeView")

        val app = application as? BigTypeApp
            ?: throw IllegalStateException("Application is not BigTypeApp")

        // Access companion properties before creating ViewModel to avoid capture issues
        val llmClient = BigTypeApp.llmClient
        val bridgeClient = BigTypeApp.bridgeClient
        val modifierManager = BigTypeApp.modifierStateManager

        val viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return KeyboardViewModel(
                        llmClient = llmClient,
                        bridgeClient = bridgeClient,
                        modifierManager = modifierManager,
                    ) as T
                }
            }
        ).get(KeyboardViewModel::class.java)

        Log.d("BigType", "IMEService: ViewModel created, building ComposeView")

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
                        Log.d("BigType", "Text input: $text")
                    },
                    onDelete = {
                        val ic = currentInputConnection
                        if (ic != null) { ic.deleteSurroundingText(1, 0) }
                        Log.d("BigType", "Delete pressed")
                    },
                    onKeyEvent = { keyCode ->
                        val ic = currentInputConnection
                        if (ic != null) {
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
                        }
                        Log.d("BigType", "Key event: $keyCode")
                    }
                )
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d("BigType", "IMEService.onStartInput() called, restarting=$restarting")
    }

    override fun onDestroy() {
        Log.d("BigType", "IMEService.onDestroy() called")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        super.onDestroy()
    }
}
