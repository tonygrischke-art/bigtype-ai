package com.aetheria.bigtype.llm

/** Structured error + result types for LLM/Bridge clients. */

sealed class ClientError : Exception() {
    data class NetworkError(override val cause: Throwable, val retryable: Boolean = true) : ClientError() {
        override val message: String get() = "Network error: ${cause.message}"
    }
    data class TimeoutError(val durationMs: Long) : ClientError() {
        override val message: String get() = "Timeout after ${durationMs}ms"
    }
    data class ParseError(val response: String, override val cause: Throwable) : ClientError() {
        override val message: String get() = "Parse error: ${cause.message}"
    }
    data class AuthError(val msg: String) : ClientError() {
        override val message: String get() = msg
    }
    data class ConfigError(val msg: String) : ClientError() {
        override val message: String get() = msg
    }

    fun isRetryable(): Boolean =
        (this is NetworkError && retryable) || this is TimeoutError
}

sealed class ClientResult<out T> {
    data class Success<T>(val data: T) : ClientResult<T>()
    data class Failure(val error: ClientError) : ClientResult<Nothing>()
    data class Offline<T>(val cached: T? = null) : ClientResult<T>()
}
