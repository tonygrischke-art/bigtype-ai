package com.aetheria.bigtype.llm

import com.aetheria.bigtype.config.ClientConfig
import com.aetheria.bigtype.config.DefaultClientConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class LLMClient(
    private val config: ClientConfig = DefaultClientConfig(),
    private val retryPolicy: RetryPolicy = RetryPolicy()
) {

    suspend fun ping(): Boolean = withContext(Dispatchers.IO) {
        try {
            val conn = URL("${config.llmUrl}/ping").openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.requestMethod = "GET"
            conn.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCompletions(prompt: String): ClientResult<List<String>> {
        val safePrompt = InputSanitizer.sanitizeForPrompt(prompt)
        val body = JSONObject().apply {
            put("prompt", safePrompt)
            put("n_predict", 50)
            put("temperature", 0.7)
        }.toString()
        return retryPolicy.execute {
            val result = post("${config.llmUrl}/completion", body)
            val content = JSONObject(result).optString("content", "")
            if (content.isNotEmpty()) listOf(content.trim()) else emptyList()
        }
    }

    suspend fun rewrite(text: String, vibe: String): ClientResult<String> {
        val safeText = InputSanitizer.sanitizeForPrompt(text)
        val prompt = when (vibe) {
            "PROFESSIONAL" -> "Rewrite professionally, reply ONLY with rewritten text: $safeText"
            "CASUAL" -> "Rewrite casually, reply ONLY with rewritten text: $safeText"
            "SNARKY" -> "Rewrite sarcastically, reply ONLY with rewritten text: $safeText"
            "ROAST" -> "Roast this savage style, reply ONLY with roast: $safeText"
            else -> "Rewrite: $safeText"
        }
        val body = JSONObject().apply {
            put("prompt", prompt)
            put("n_predict", 100)
            put("temperature", 0.9)
        }.toString()
        return retryPolicy.execute {
            val result = post("${config.llmUrl}/completion", body)
            JSONObject(result).optString("content", "")
        }
    }

    suspend fun generateCommitMessage(diff: String): ClientResult<String> {
        val safeDiff = InputSanitizer.sanitizeForPrompt(diff.take(MAX_DIFF_CHARS))
        val body = JSONObject().apply {
            put("prompt", "Write a concise git commit message for this diff. Reply ONLY with the message:\n$safeDiff")
            put("n_predict", 60)
            put("temperature", 0.5)
        }.toString()
        return retryPolicy.execute {
            val result = post("${config.llmUrl}/completion", body)
            JSONObject(result).optString("content", "").trim()
        }
    }

    private fun post(url: String, body: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = config.llmTimeoutMs.toInt()
        conn.readTimeout = config.llmTimeoutMs.toInt()
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")

        try {
            conn.outputStream.use { it.write(body.toByteArray()) }
            return when (val code = conn.responseCode) {
                200 -> conn.inputStream.bufferedReader().readText()
                in 400..499 -> throw ClientError.AuthError("LLM API error: $code")
                in 500..599 -> throw IOException("LLM server error: $code")
                else -> throw IOException("Unexpected response: $code")
            }
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        private const val MAX_DIFF_CHARS = 2000
    }
}
