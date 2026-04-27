package com.aetheria.bigtype.keyboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class ModifierType { CTRL, SHIFT, ALT }

data class ModifierState(
    val activeModifiers: Set<ModifierType> = emptySet(),
    val isCtrlLocked: Boolean = false,
    val isShiftLocked: Boolean = false,
    val isAltLocked: Boolean = false
)

@Singleton
class ModifierStateManager @Inject constructor() {
    private val _state = MutableStateFlow(ModifierState())
    val state: StateFlow<ModifierState> = _state

    fun onModifierPressed(type: ModifierType) {
        _state.value = when (type) {
            ModifierType.CTRL -> toggle(_state.value, type, _state.value.isCtrlLocked) { v, locked -> v.copy(isCtrlLocked = locked) }
            ModifierType.SHIFT -> toggle(_state.value, type, _state.value.isShiftLocked) { v, locked -> v.copy(isShiftLocked = locked) }
            ModifierType.ALT -> toggle(_state.value, type, _state.value.isAltLocked) { v, locked -> v.copy(isAltLocked = locked) }
        }
    }

    private fun toggle(
        current: ModifierState,
        type: ModifierType,
        isLocked: Boolean,
        setLocked: (ModifierState, Boolean) -> ModifierState
    ): ModifierState {
        return when {
            isLocked -> setLocked(current.copy(activeModifiers = current.activeModifiers - type), false)
            current.activeModifiers.contains(type) -> setLocked(current, true)
            else -> current.copy(activeModifiers = current.activeModifiers + type)
        }
    }
}