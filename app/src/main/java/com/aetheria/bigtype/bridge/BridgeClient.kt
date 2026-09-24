package com.aetheria.bigtype.bridge

import android.util.Log
import com.aetheria.bigtype.config.ClientConfig
import com.aetheria.bigtype.config.DefaultClientConfig
import com.aetheria.bigtype.llm.InputSanitizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class BridgeClient(
    private val config: ClientConfig = DefaultClientConfig()
) {

    suspend fun ping(): Boolean = withContext(Dispatchers.IO) {
        try {
            val conn = URL("${config.bridgeUrl}/ping").openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.requestMethod = "GET"
            conn.responseCode == 200
        } catch (e: Exception) {
            Log.w(TAG, "Bridge ping failed: ${e.message}")
            false
        }
    }

    suspend fun searchSnippet(query: String): String = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("action", "search_snippet")
            put("query", InputSanitizer.sanitizeForPrompt(query))
        }.toString()
        call("${config.bridgeUrl}/keyboard", body, "result", "searchSnippet")
    }

    suspend fun syncClipboard(action: String, content: String): String = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("action", action)
            put("content", content)
        }.toString()
        call("${config.bridgeUrl}/clipboard", body, "result", "syncClipboard")
    }

    suspend fun getGitStatus(): String = withContext(Dispatchers.IO) {
        val body = JSONObject().apply { put("action", "git_status") }.toString()
        call("${config.bridgeUrl}/git", body, "output", "getGitStatus")
    }

    suspend fun getGitDiff(): String = withContext(Dispatchers.IO) {
        val body = JSONObject().apply { put("action", "git_diff") }.toString()
        call("${config.bridgeUrl}/git", body, "output", "getGitDiff")
    }

    private fun call(url: String, body: String, responseKey: String, op: String): String {
        return try {
            val result = post(url, body)
            if (result.isNotEmpty()) JSONObject(result).optString(responseKey, "") else ""
        } catch (e: Exception) {
            Log.w(TAG, "$op failed: ${e.message}")
            ""
        }
    }

    private fun post(url: String, body: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = config.bridgeTimeoutMs.toInt()
        conn.readTimeout = BRIDGE_READ_TIMEOUT_MS.toInt()
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        try {
            conn.outputStream.use { it.write(body.toByteArray()) }
            return if (conn.responseCode == 200)
                conn.inputStream.bufferedReader().readText() else ""
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        private const val TAG = "BigType"
        private const val BRIDGE_READ_TIMEOUT_MS = 15000L
    }
}
