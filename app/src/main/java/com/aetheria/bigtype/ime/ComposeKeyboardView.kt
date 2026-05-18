package com.aetheria.bigtype.ime

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
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

/**
 * A [AbstractComposeView] that implements [LifecycleOwner], [SavedStateRegistryOwner],
 * and [ViewModelStoreOwner] so that Compose can function correctly inside an
 * [android.inputmethodservice.InputMethodService] which has no built-in lifecycle owners.
 *
 * Lifecycle transitions must be driven by the IME service:
 * - [onResume] → move to RESUMED (call from [android.inputmethodservice.InputMethodService.onStartInputView])
 * - [onPause]  → move to STARTED  (call from [android.inputmethodservice.InputMethodService.onFinishInputView])
 * - [onStop]   → move to STOPPED
 * - [onDestroy] → move to DESTROYED
 */
class ComposeKeyboardView(
    context: Context,
    private val content: @Composable () -> Unit
) : AbstractComposeView(context),
    LifecycleOwner,
    SavedStateRegistryOwner,
    ViewModelStoreOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    init {
        // Perform initial restore and set CREATED state
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        // Wire the view tree so Compose can find the owners
        setViewTreeLifecycleOwner(this)
        setViewTreeSavedStateRegistryOwner(this)
        setViewTreeViewModelStoreOwner(this)
    }

    // ── LifecycleOwner ──────────────────────────────────────────────────

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    // ── SavedStateRegistryOwner ─────────────────────────────────────────

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    // ── ViewModelStoreOwner ─────────────────────────────────────────────

    override val viewModelStore: ViewModelStore
        get() = store

    // ── External lifecycle hooks called by the IME service ─────────────

    /** Call from [android.inputmethodservice.InputMethodService.onStartInputView]. */
    fun onResume() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    /** Call from [android.inputmethodservice.InputMethodService.onFinishInputView]. */
    fun onPause() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    /** Call when the view is detached from the window or when input is finished. */
    fun onStop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    /** Call to fully destroy. Clears the ViewModelStore. */
    fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    // ── AbstractComposeView ─────────────────────────────────────────────

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        shouldCreateCompositionOnAttachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // The IME service will drive the STOP event explicitly,
        // but guard against being re-attached without a fresh lifecycle.
    }

    @Composable
    override fun Content() {
        content()
    }
}