package com.aetheria.bigtype.config

import android.content.Context
import com.aetheria.bigtype.BuildConfig
import com.aetheria.bigtype.llm.InputSanitizer

interface ClientConfig {
    val llmUrl: String
    val bridgeUrl: String
    val llmTimeoutMs: Long
    val bridgeTimeoutMs: Long
}

/** Defaults from BuildConfig; no context required. Debug builds can override via prefs. */
open class DefaultClientConfig : ClientConfig {
    override val llmUrl: String get() = BuildConfig.LLM_URL
    override val bridgeUrl: String get() = BuildConfig.BRIDGE_URL
    override val llmTimeoutMs: Long get() = BuildConfig.LLM_TIMEOUT_MS
    override val bridgeTimeoutMs: Long get() = BuildConfig.BRIDGE_TIMEOUT_MS
}

/** SharedPreferences-backed overrides, only honored in debug builds. */
class ClientConfigImpl(context: Context) : DefaultClientConfig() {
    private val prefs = context.getSharedPreferences("client_config", Context.MODE_PRIVATE)

    override val llmUrl: String
        get() = overrideString("llm_url") ?: super.llmUrl

    override val bridgeUrl: String
        get() = overrideString("bridge_url") ?: super.bridgeUrl

    private fun overrideString(key: String): String? =
        if (BuildConfig.DEBUG) prefs.getString(key, null) else null

    /** Debug-only: override an endpoint at runtime after validation. */
    fun setOverride(type: String, newUrl: String): Boolean {
        if (!BuildConfig.DEBUG) return false
        return InputSanitizer.validateUrl(newUrl)
            .map { prefs.edit().putString("${type}_url", it).apply() }
            .isSuccess
    }
}
