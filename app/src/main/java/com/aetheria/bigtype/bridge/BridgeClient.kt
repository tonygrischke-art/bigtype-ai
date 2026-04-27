package com.aetheria.bigtype.bridge

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BridgeClient @Inject constructor() {
    private val baseUrl = "http://localhost:8000"

    suspend fun ping(): Boolean = withContext(Dispatchers.IO) {
        try {
            val conn = URL("$baseUrl/ping").openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.requestMethod = "GET"
            conn.responseCode == 200
        } catch (e: Exception) { false }
    }

    suspend fun searchSnippet(query: String): String = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("action", "search_snippet")
                put("query", query)
            }.toString()
            val result = post("$baseUrl/keyboard", body)
            if (result.isNotEmpty()) JSONObject(result).optString("result", "") else ""
        } catch (e: Exception) { "" }
    }

    suspend fun syncClipboard(action: String, content: String): String = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("action", action)
                put("content", content)
            }.toString()
            val result = post("$baseUrl/clipboard", body)
            if (result.isNotEmpty()) JSONObject(result).optString("result", "") else ""
        } catch (e: Exception) { "" }
    }

    suspend fun getGitStatus(): String = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("action", "git_status")
            }.toString()
            val result = post("$baseUrl/git", body)
            if (result.isNotEmpty()) JSONObject(result).optString("output", "") else ""
        } catch (e: Exception) { "" }
    }

    suspend fun getGitDiff(): String = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("action", "git_diff")
            }.toString()
            val result = post("$baseUrl/git", body)
            if (result.isNotEmpty()) JSONObject(result).optString("output", "") else ""
        } catch (e: Exception) { "" }
    }

    private fun post(url: String, body: String): String {
        return try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 15000
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.use { it.write(body.toByteArray()) }
            if (conn.responseCode == 200) conn.inputStream.bufferedReader().readText() else ""
        } catch (e: Exception) { "" }
    }
}