package com.aetheria.bigtype.llm

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Coroutine timeout wrapper that converts timeouts into ClientResult.Failure
 * with structured logging. Re-throws CancellationException so coroutine
 * cancellation (lifecycle, new input) always propagates.
 */
suspend fun <T> withTimeoutAndLogging(
    timeoutMs: Long,
    operation: String,
    block: suspend () -> T
): ClientResult<T> = try {
    val result = withTimeoutOrNull(timeoutMs) { block() }
    if (result != null) {
        ClientResult.Success(result)
    } else {
        Log.w(TAG, "Operation '$operation' timed out after ${timeoutMs}ms")
        ClientResult.Failure(ClientError.TimeoutError(timeoutMs))
    }
} catch (e: CancellationException) {
    Log.d(TAG, "Operation '$operation' cancelled")
    throw e
} catch (e: Exception) {
    Log.e(TAG, "Operation '$operation' failed", e)
    ClientResult.Failure(ClientError.NetworkError(e))
}

/** Timeouts: IME-visible requests must feel instant; background ops may wait. */
object OperationTimeouts {
    const val IME_MS = 2000L
    const val BACKGROUND_MS = 10000L
}

private const val TAG = "BigType"
