package com.aetheria.bigtype.llm

import java.net.URI

/** Sanitizes user text and validates URLs before they reach LLM prompts or network calls. */
object InputSanitizer {

    /** Escape a string for safe embedding inside JSON values / prompt text. */
    fun escapeJson(str: String): String =
        str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")

    /** Reduce prompt-injection surface: neutralize common control phrases. */
    fun sanitizeForPrompt(str: String): String =
        str.replace("IGNORE ALL PREVIOUS INSTRUCTIONS", "", ignoreCase = true)
           .replace("SYSTEM:", "", ignoreCase = true)
           .replace("ASSISTANT:", "", ignoreCase = true)
           .trim()

    fun validateUrl(url: String): Result<String> {
        return try {
            val uri = URI(url)
            require(uri.scheme == "http" || uri.scheme == "https") { "Scheme must be http/https" }
            require(!uri.host.isNullOrEmpty()) { "Host required" }
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Invalid URL: $url", e))
        }
    }
}
