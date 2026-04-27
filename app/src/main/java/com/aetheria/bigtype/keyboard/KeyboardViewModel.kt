package com.aetheria.bigtype.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetheria.bigtype.bridge.BridgeClient
import com.aetheria.bigtype.llm.LLMClient
import com.aetheria.bigtype.privacy.PrivacyDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class VibeMode(val emoji: String) {
    PROFESSIONAL("💼"),
    CASUAL("😊"),
    SNARKY("😏"),
    ROAST("🔥")
}

enum class ThemeMode { DARK_GLASS, NEON, MINIMAL_WHITE, AETHERIA }
enum class BridgeStatus { ONLINE, PARTIAL, OFFLINE }
enum class LLMStatus { ONLINE, OFFLINE }

data class KeyboardState(
    val currentText: String = "",
    val suggestions: List<String> = emptyList(),
    val vibe: VibeMode = VibeMode.CASUAL,
    val theme: ThemeMode = ThemeMode.DARK_GLASS,
    val isHexLayout: Boolean = false,
    val isTerminalOpen: Boolean = false,
    val bridgeStatus: BridgeStatus = BridgeStatus.OFFLINE,
    val llmStatus: LLMStatus = LLMStatus.OFFLINE,
    val modifierState: ModifierState = ModifierState(),
    val isOneHanded: Boolean = false,
    val oneHandedSide: String = "left",
    val isTranslateMode: Boolean = false,
    val translation: String = "",
    val recentEmojis: List<String> = emptyList(),
    val predictedEmojis: List<String> = emptyList(),
    val isDevMode: Boolean = false,
    val isPrivacyMode: Boolean = false,
    val showNumberRow: Boolean = false,
    val rewriteResult: String = "",
    val isRewriting: Boolean = false,
    val snippetQuery: String = "",
    val isSnippetMode: Boolean = false,
    val gitBranch: String = "",
    val smartReplies: List<String> = emptyList(),
    val wpm: Int = 0
)

@OptIn(FlowPreview::class)
@HiltViewModel
class KeyboardViewModel @Inject constructor(
    private val llmClient: LLMClient,
    private val bridgeClient: BridgeClient,
    private val modifierManager: ModifierStateManager,
    private val glideDecoder: GlideDecoder,
    private val appProfileManager: AppProfileManager,
    private val translateEngine: TranslateEngine,
    private val emojiPredictor: EmojiPredictor,
    private val autocorrectEngine: AutocorrectEngine,
    private val privacyDetector: PrivacyDetector
) : ViewModel() {

    private val _state = MutableStateFlow(KeyboardState())
    val state: StateFlow<KeyboardState> = _state

    init {
        checkServicesStatus()
        setupSuggestionDebounce()
    }

    private fun setupSuggestionDebounce() {
        _state
            .debounce(400)
            .onEach { state ->
                if (!state.isPrivacyMode && state.currentText.length >= 2) {
                    fetchSuggestions(state.currentText, state.vibe)
                    generateSmartReplies(state.currentText)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkServicesStatus() {
        viewModelScope.launch {
            val llmOnline = llmClient.ping()
            val bridgeOnline = bridgeClient.ping()
            _state.value = _state.value.copy(
                llmStatus = if (llmOnline) LLMStatus.ONLINE else LLMStatus.OFFLINE,
                bridgeStatus = if (bridgeOnline) BridgeStatus.ONLINE else BridgeStatus.OFFLINE
            )
        }
    }

    private fun fetchSuggestions(text: String, vibe: VibeMode) {
        viewModelScope.launch {
            val prompt = "Give 3 short ${vibe.name.lowercase()} completions for: \"$text\". Reply ONLY with completions separated by |"
            val results = llmClient.getCompletions(prompt)
            val suggestions = results.firstOrNull()?.split("|")?.map { it.trim() }?.take(3) ?: emptyList()
            emojiPredictor.predict(text)
            _state.value = _state.value.copy(
                suggestions = suggestions,
                predictedEmojis = emojiPredictor.predictedEmojis.value
            )
        }
    }

    private fun generateSmartReplies(text: String) {
        viewModelScope.launch {
            val replies = when {
                text.contains("?") -> listOf("Yes!", "No, sorry", "Let me check")
                text.lowercase().contains("hello") || text.lowercase().contains("hi") ->
                    listOf("Hey!", "Hi there!", "Hello!")
                text.lowercase().contains("thanks") -> listOf("You're welcome!", "No problem!", "Anytime!")
                else -> emptyList()
            }
            _state.value = _state.value.copy(smartReplies = replies)
        }
    }

    fun onTextChanged(newText: String) {
        if (_state.value.isPrivacyMode) return
        if (newText.contains ";;" ) {
            val query = newText.substringAfter(";;")
            _state.value = _state.value.copy(
                currentText = newText,
                isSnippetMode = true,
                snippetQuery = query
            )
            return
        }
        _state.value = _state.value.copy(currentText = newText, isSnippetMode = false)
    }

    fun setVibe(vibe: VibeMode) {
        _state.value = _state.value.copy(vibe = vibe)
    }

    fun cycleVibe() {
        val values = VibeMode.values()
        val next = values[(values.indexOf(_state.value.vibe) + 1) % values.size]
        _state.value = _state.value.copy(vibe = next)
    }

    fun setTheme(theme: ThemeMode) {
        _state.value = _state.value.copy(theme = theme)
    }

    fun rewriteSelectedText(selectedText: String) {
        if (_state.value.isPrivacyMode || selectedText.isEmpty()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isRewriting = true)
            val result = llmClient.rewrite(selectedText, _state.value.vibe.name)
            _state.value = _state.value.copy(rewriteResult = result, isRewriting = false)
        }
    }

    fun clearRewriteResult() {
        _state.value = _state.value.copy(rewriteResult = "")
    }

    fun toggleLayout() {
        _state.value = _state.value.copy(isHexLayout = !_state.value.isHexLayout)
    }

    fun toggleTerminal() {
        _state.value = _state.value.copy(isTerminalOpen = !_state.value.isTerminalOpen)
    }

    fun toggleNumberRow() {
        _state.value = _state.value.copy(showNumberRow = !_state.value.showNumberRow)
    }

    fun toggleOneHanded() {
        _state.value = _state.value.copy(isOneHanded = !_state.value.isOneHanded)
    }

    fun toggleOneHandedSide() {
        val side = if (_state.value.oneHandedSide == "left") "right" else "left"
        _state.value = _state.value.copy(oneHandedSide = side)
    }

    fun toggleTranslateMode() {
        _state.value = _state.value.copy(isTranslateMode = !_state.value.isTranslateMode)
    }

    fun onEmojiSelected(emoji: String) {
        _state.value = _state.value.copy(
            recentEmojis = (_state.value.recentEmojis + emoji).takeLast(20)
        )
    }

    fun onGlideMove(keyLabel: String) {
        glideDecoder.onGlideMove(keyLabel)
    }

    fun onGlideEnd(): String = glideDecoder.onGlideEnd()

    fun loadAppProfile(packageName: String) {
        viewModelScope.launch {
            appProfileManager.loadProfile(packageName)
            val vibe = appProfileManager.getVibeForApp(packageName)
            _state.value = _state.value.copy(vibe = vibe)
        }
    }

    fun detectSecureMode(inputType: Int) {
        val isSecure = privacyDetector.isSecureField(inputType)
        _state.value = _state.value.copy(isPrivacyMode = isSecure)
    }

    fun generateCommitMessage() {
        viewModelScope.launch {
            val diff = bridgeClient.getGitDiff()
            if (diff.isNotEmpty()) {
                val message = llmClient.generateCommitMessage(diff)
                _state.value = _state.value.copy(rewriteResult = message)
            }
        }
    }
}