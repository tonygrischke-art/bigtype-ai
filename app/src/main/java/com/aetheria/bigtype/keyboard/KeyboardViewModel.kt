package com.aetheria.bigtype.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetheria.bigtype.bridge.BridgeClient
import com.aetheria.bigtype.llm.ClientResult
import com.aetheria.bigtype.llm.LLMClient
import com.aetheria.bigtype.llm.OperationTimeouts
import com.aetheria.bigtype.llm.withTimeoutAndLogging
import com.aetheria.bigtype.privacy.PrivacyDetector
import com.aetheria.bigtype.privacy.SecureLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.aetheria.bigtype.keyboard.ThemeMode

enum class VibeMode(val emoji: String) {
    PROFESSIONAL("💼"),
    CASUAL("😊"),
    SNARKY("😏"),
    ROAST("🔥")
}

enum class BridgeStatus { ONLINE, PARTIAL, OFFLINE }
enum class LLMStatus { ONLINE, OFFLINE }
enum class PrivacyReason { NONE, SECURE_FIELD, BANKING_APP, USER_DISABLED }

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
    val privacyReason: PrivacyReason = PrivacyReason.NONE,
    val isLoadingSuggestions: Boolean = false,
    val showNumberRow: Boolean = false,
    val rewriteResult: String = "",
    val isRewriting: Boolean = false,
    val gitBranch: String = "",
    val smartReplies: List<String> = emptyList()
)

class KeyboardViewModel(
    private val llmClient: LLMClient,
    private val bridgeClient: BridgeClient,
    val modifierManager: ModifierStateManager
) : ViewModel() {

    private val _state = MutableStateFlow(KeyboardState())
    val state: StateFlow<KeyboardState> = _state

    private val glideDecoder = GlideDecoder()
    private val appProfileManager = AppProfileManager()
    private val emojiPredictor = EmojiPredictor()
    private val autocorrectEngine = AutocorrectEngine(
        runCatching { com.aetheria.bigtype.BigTypeApp.database }.getOrNull()
    )
    private val privacyDetector = PrivacyDetector()
    private val translateEngine = TranslateEngine(llmClient)

    init {
        checkServicesStatus()
        viewModelScope.launch { autocorrectEngine.warmCache() }
    }

    /** Learning: call when the user accepts an autocorrection. */
    fun onCorrectionAccepted(original: String) {
        viewModelScope.launch { autocorrectEngine.learnFromAcceptance(original) }
    }

    /** Learning: call when the user deletes/rejects an autocorrection. */
    fun onCorrectionRejected(original: String) {
        viewModelScope.launch { autocorrectEngine.learnFromDeletion(original) }
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
        if (_state.value.isPrivacyMode) {
            SecureLogger.d("Suggestions skipped (privacy mode)", isPrivate = true)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingSuggestions = true)
            val prompt = "Give 3 short ${vibe.name.lowercase()} completions for: \"$text\". Reply ONLY with completions separated by |"
            val timed = withTimeoutAndLogging(OperationTimeouts.IME_MS, "IME completions") {
                llmClient.getCompletions(prompt)
            }
            val result: ClientResult<List<String>> = when (timed) {
                is ClientResult.Success -> timed.data
                is ClientResult.Failure -> timed
                is ClientResult.Offline -> ClientResult.Offline(null)
            }
            when (result) {
                is ClientResult.Success -> {
                    val suggestions = result.data.firstOrNull()
                        ?.split("|")?.map { it.trim() }?.take(3) ?: emptyList()
                    emojiPredictor.predict(text)
                    _state.value = _state.value.copy(
                        suggestions = suggestions,
                        predictedEmojis = emojiPredictor.predictedEmojis.value,
                        isLoadingSuggestions = false,
                        llmStatus = LLMStatus.ONLINE
                    )
                }
                is ClientResult.Failure -> {
                    SecureLogger.e("Suggestion fetch failed: ${result.error.message}", isPrivate = true)
                    _state.value = _state.value.copy(
                        suggestions = emptyList(),
                        isLoadingSuggestions = false,
                        llmStatus = LLMStatus.OFFLINE
                    )
                }
                is ClientResult.Offline -> {
                    _state.value = _state.value.copy(
                        isLoadingSuggestions = false,
                        llmStatus = LLMStatus.OFFLINE
                    )
                }
            }
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
            val timed = withTimeoutAndLogging(OperationTimeouts.BACKGROUND_MS, "Rewrite") {
                llmClient.rewrite(selectedText, _state.value.vibe.name)
            }
            val result: ClientResult<String> = when (timed) {
                is ClientResult.Success -> timed.data
                is ClientResult.Failure -> timed
                is ClientResult.Offline -> ClientResult.Offline(null)
            }
            when (result) {
                is ClientResult.Success ->
                    _state.value = _state.value.copy(rewriteResult = result.data, isRewriting = false)
                is ClientResult.Failure -> {
                    SecureLogger.e("Rewrite failed: ${result.error.message}", isPrivate = true)
                    _state.value = _state.value.copy(rewriteResult = "", isRewriting = false)
                }
                is ClientResult.Offline ->
                    _state.value = _state.value.copy(
                        rewriteResult = result.cached ?: "",
                        isRewriting = false
                    )
            }
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
        val isBanking = privacyDetector.isBankingField(inputType)
        val (isPrivate, reason) = when {
            isSecure -> true to PrivacyReason.SECURE_FIELD
            isBanking -> true to PrivacyReason.BANKING_APP
            else -> false to PrivacyReason.NONE
        }
        _state.value = _state.value.copy(
            isPrivacyMode = isPrivate,
            privacyReason = reason,
            llmStatus = if (isPrivate) LLMStatus.OFFLINE else _state.value.llmStatus,
            suggestions = if (isPrivate) emptyList() else _state.value.suggestions,
            smartReplies = if (isPrivate) emptyList() else _state.value.smartReplies,
            isDevMode = if (isPrivate) false else _state.value.isDevMode
        )
        SecureLogger.d("Privacy mode: $isPrivate (reason: $reason)", isPrivate = true)
    }

    fun setPrivacyModeByUser(enabled: Boolean) {
        _state.value = _state.value.copy(
            isPrivacyMode = enabled,
            privacyReason = if (enabled) PrivacyReason.USER_DISABLED else PrivacyReason.NONE,
            llmStatus = if (enabled) LLMStatus.OFFLINE else _state.value.llmStatus,
            suggestions = if (enabled) emptyList() else _state.value.suggestions
        )
    }

    fun generateCommitMessage() {
        viewModelScope.launch {
            val diff = bridgeClient.getGitDiff()
            if (diff.isNotEmpty()) {
                val timed = withTimeoutAndLogging(OperationTimeouts.BACKGROUND_MS, "Commit message") {
                    llmClient.generateCommitMessage(diff)
                }
                val result: ClientResult<String> = when (timed) {
                    is ClientResult.Success -> timed.data
                    is ClientResult.Failure -> timed
                    is ClientResult.Offline -> ClientResult.Offline(null)
                }
                when (result) {
                    is ClientResult.Success ->
                        _state.value = _state.value.copy(rewriteResult = result.data)
                    is ClientResult.Failure ->
                        SecureLogger.e("Commit message failed: ${result.error.message}")
                    is ClientResult.Offline ->
                        _state.value = _state.value.copy(rewriteResult = result.cached ?: "")
                }
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