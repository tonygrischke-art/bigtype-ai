package com.aetheria.bigtype.privacy

import android.util.Log

/** Logging wrapper that never leaks typed text when privacy mode is active. */
object SecureLogger {
    private const val TAG = "BigType"

    fun d(msg: String, text: String? = null, isPrivate: Boolean = false) {
        Log.d(TAG, redact(msg, text, isPrivate))
    }

    fun w(msg: String, text: String? = null, isPrivate: Boolean = false) {
        Log.w(TAG, redact(msg, text, isPrivate))
    }

    fun e(msg: String, tr: Throwable? = null, isPrivate: Boolean = false) {
        val safe = if (isPrivate) "$msg [REDACTED]" else msg
        if (tr != null) Log.e(TAG, safe, tr) else Log.e(TAG, safe)
    }

    private fun redact(msg: String, text: String?, isPrivate: Boolean): String = when {
        isPrivate -> "$msg [REDACTED]"
        text != null -> "$msg: $text"
        else -> msg
    }
}
