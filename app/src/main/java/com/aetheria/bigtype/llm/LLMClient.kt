package com.aetheria.bigtype.llm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LLMClient @Inject constructor() {
    private val baseUrl = "http://localhost:8080"

    suspend fun ping(): Boolean = withContext(Dispatchers.IO) {
        try {
            val conn = URL("$baseUrl/ping").openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.requestMethod = "GET"
            conn.responseCode == 200
        } catch (e: Exception) { false }
    }

    suspend fun getCompletions(prompt: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("prompt", prompt)
                put("n_predict", 50)
                put("temperature", 0.7)
                put("stop", JSONArray().put("\n"))
            }.toString()
            val result = post("$baseUrl/completion", body)
            if (result.isNotEmpty()) {
                val json = JSONObject(result)
                val content = json.optString("content", "")
                if (content.isNotEmpty()) listOf(content.trim()) else emptyList()
            } else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun rewrite(text: String, vibe: String): String = withContext(Dispatchers.IO) {
        try {
            val prompt = when (vibe) {
                "PROFESSIONAL" -> "Rewrite professionally, reply ONLY with rewritten text: $text"
                "CASUAL" -> "Rewrite casually, reply ONLY with rewritten text: $text"
                "SNARKY" -> "Rewrite sarcastically, reply ONLY with rewritten text: $text"
                "ROAST" -> "Roast this savage style, reply ONLY with roast: $text"
                else -> "Rewrite: $text"
            }
            val body = JSONObject().apply {
                put("prompt", prompt)
                put("n_predict", 100)
                put("temperature", 0.9)
            }.toString()
            val result = post("$baseUrl/completion", body)
            if (result.isNotEmpty()) JSONObject(result).optString("content", "") else ""
        } catch (e: Exception) { "" }
    }

    suspend fun generateCommitMessage(diff: String): String = withContext(Dispatchers.IO) {
        try {
            val prompt = "Write a concise git commit message for this diff. Reply ONLY with the message:\n$diff"
            val body = JSONObject().apply {
                put("prompt", prompt)
                put("n_predict", 60)
                put("temperature", 0.5)
            }.toString()
            val result = post("$baseUrl/completion", body)
            if (result.isNotEmpty()) JSONObject(result).optString("content", "").trim() else ""
        } catch (e: Exception) { "" }
    }

    private fun post(url: String, body: String): String {
        return try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 20000
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.use { it.write(body.toByteArray()) }
            if (conn.responseCode == 200) conn.inputStream.bufferedReader().readText() else ""
        } catch (e: Exception) { "" }
    }
}