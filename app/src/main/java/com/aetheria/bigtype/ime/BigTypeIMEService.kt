package com.aetheria.bigtype.ime

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import com.aetheria.bigtype.BigTypeApp
import com.aetheria.bigtype.keyboard.KeyboardViewModel
import com.aetheria.bigtype.ui.theme.BigTypeTheme
import com.aetheria.bigtype.keyboard.ThemeMode

class BigTypeIMEService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    override fun onCreate() {
        super.onCreate()
        Log.d("BigType", "IMEService.onCreate()")
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        setCandidatesViewShown(false)
        Log.d("BigType", "IMEService.onCreate() finished")
    }

    override fun onCreateInputView(): View {
        Log.d("BigType", "IMEService.onCreateInputView() START")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        return try {
            val app = application as? BigTypeApp
            if (app == null) {
                Log.e("BigType", "Application is not BigTypeApp!")
                return createErrorView("App init error")
            }

            Log.d("BigType", "Creating ViewModel...")
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

            Log.d("BigType", "ViewModel created. Building ComposeView...")

            val composeView = ComposeView(this).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                setViewTreeLifecycleOwner(this@BigTypeIMEService)
                setViewTreeSavedStateRegistryOwner(this@BigTypeIMEService)
                setViewTreeViewModelStoreOwner(this@BigTypeIMEService)
                setContent {
                    BigTypeTheme(themeMode = ThemeMode.DARK_GLASS) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0D0F1A))
                        ) {
                            // Simple test row - if this shows, Compose works
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text("BigType AI", color = Color(0xFF00E5FF), fontSize = 14.sp)
                                Text("Keyboard", color = Color(0xFF7986CB), fontSize = 14.sp)
                            }
                            // Full keyboard
                            BigTypeKeyboardScreen(
                                viewModel = viewModel,
                                onTextInput = { text ->
                                    currentInputConnection?.commitText(text, 1)
                                },
                                onDelete = {
                                    currentInputConnection?.deleteSurroundingText(1, 0)
                                },
                                onKeyEvent = { keyCode ->
                                    currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
                                    currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
                                }
                            )
                        }
                    }
                }
            }

            Log.d("BigType", "ComposeView created successfully!")
            composeView

        } catch (e: Exception) {
            Log.e("BigType", "CRASH in onCreateInputView: ${e.message}", e)
            createErrorView("Error: ${e.message}")
        }
    }

    private fun createErrorView(message: String): View {
        return ComposeView(this).apply {
            setContent {
                BigTypeTheme(themeMode = ThemeMode.DARK_GLASS) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFF0D0F1A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "BigType AI\n$message",
                            color = Color(0xFFEF5350),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d("BigType", "onStartInput restarting=$restarting")
    }

    override fun onDestroy() {
        Log.d("BigType", "IMEService.onDestroy()")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        super.onDestroy()
    }
}
