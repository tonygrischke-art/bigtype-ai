package com.aetheria.bigtype.clipboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ClipType { TEXT, URL, CODE, EMAIL, PHONE }

data class ClipItem(
    val id: Long = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val type: ClipType = ClipType.TEXT
)

data class ClipboardState(
    val clips: List<ClipItem> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: Int = 0
)

@HiltViewModel
class ClipboardManager @Inject constructor(
    private val db: BigTypeDatabase
) : ViewModel() {
    private val _state = MutableStateFlow(ClipboardState())
    val state: StateFlow<ClipboardState> = _state

    init { loadClips() }

    private fun loadClips() {
        viewModelScope.launch {
            db.clipDao().getAllClips().collect { clips ->
                _state.update { it.copy(clips = clips.map { entity ->
                    ClipItem(
                        id = entity.id,
                        text = entity.text,
                        timestamp = entity.timestamp,
                        isPinned = entity.isPinned,
                        type = runCatching { ClipType.valueOf(entity.type) }.getOrDefault(ClipType.TEXT)
                    )
                }) }
            }
        }
    }

    fun addClip(text: String) {
        viewModelScope.launch {
            db.clipDao().insert(ClipEntity(text = text, type = detectType(text)))
        }
    }

    fun deleteClip(id: Long) {
        viewModelScope.launch { db.clipDao().delete(id) }
    }

    fun togglePin(id: Long) {
        viewModelScope.launch { db.clipDao().togglePin(id) }
    }

    private fun detectType(text: String): String = when {
        text.startsWith("http") -> ClipType.URL.name
        text.contains("@") && text.contains(".") -> ClipType.EMAIL.name
        text.matches(Regex("^[0-9+\\-() ]+$")) -> ClipType.PHONE.name
        text.contains("{") || text.contains(";") -> ClipType.CODE.name
        else -> ClipType.TEXT.name
    }
}