package com.aetheria.bigtype.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetheria.bigtype.bridge.BridgeClient
import com.aetheria.bigtype.llm.LLMClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val isOneHanded: Boolean = false,
    val oneHandedSide: String = "left",
    val isTranslateMode: Boolean = false,
    val recentEmojis: List<String> = emptyList(),
    val predictedEmojis: List<String> = emptyList(),
    val isDevMode: Boolean = false,
    val isPrivacyMode: Boolean = false,
    val showNumberRow: Boolean = false,
    val rewriteResult: String = "",
    val isRewriting: Boolean = false,
    val gitBranch: String = "",
    val smartReplies: List<String> = emptyList()
)

@HiltViewModel
class KeyboardViewModel @Inject constructor(
    private val llmClient: LLMClient,
    private val bridgeClient: BridgeClient
) : ViewModel() {

    private val _state = MutableStateFlow(KeyboardState())
    val state: StateFlow<KeyboardState> = _state

    private val modifierManager = ModifierStateManager()
    private val glideDecoder = GlideDecoder()
    private val appProfileManager = AppProfileManager()
    private val emojiPredictor = EmojiPredictor()
    private val autocorrectEngine = AutocorrectEngine()
    private val privacyDetector = PrivacyDetector()
    private val translateEngine = TranslateEngine(llmClient)

    init {
        checkServicesStatus()
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
        _state.value = _state.value.copy(currentText = newText)
        if (newText.length >= 2) {
            fetchSuggestions(newText, _state.value.vibe)
            generateSmartReplies(newText)
        }
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

    fun onDevKeyPressed(key: String) {
        when (key) {
            "DEV" -> _state.value = _state.value.copy(isDevMode = !_state.value.isDevMode)
            "HEX" -> toggleLayout()
            "TERM" -> toggleTerminal()
            "NUM" -> toggleNumberRow()
        }
    }
}