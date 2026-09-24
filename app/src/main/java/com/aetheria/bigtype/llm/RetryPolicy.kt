package com.aetheria.bigtype.llm

import android.util.Log
import kotlinx.coroutines.delay
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.random.Random

class RetryPolicy(
    val maxRetries: Int = 3,
    val initialDelayMs: Long = 100,
    val maxDelayMs: Long = 5000,
    val backoffMultiplier: Double = 2.0,
    val jitterFraction: Double = 0.1
) {
    suspend fun <T> execute(block: suspend () -> T): ClientResult<T> {
        var lastError: ClientError? = null
        var delayMs = initialDelayMs

        repeat(maxRetries + 1) { attempt ->
            try {
                return ClientResult.Success(block())
            } catch (e: SocketTimeoutException) {
                lastError = ClientError.TimeoutError(delayMs)
                Log.w(TAG, "Attempt $attempt: timeout, retrying in ${delayMs}ms")
            } catch (e: IOException) {
                lastError = ClientError.NetworkError(e)
                Log.w(TAG, "Attempt $attempt: network error, retrying", e)
            } catch (e: ClientError.AuthError) {
                Log.e(TAG, "Attempt $attempt: auth error, no retry", e)
                return ClientResult.Failure(e)
            } catch (e: Exception) {
                lastError = ClientError.ParseError(e.message ?: "", e)
                Log.e(TAG, "Attempt $attempt: parse error, no retry", e)
                return ClientResult.Failure(lastError!!)
            }

            if (attempt < maxRetries && lastError?.isRetryable() == true) {
                delay(delayMs + Random.nextLong(((delayMs * jitterFraction).toLong()).coerceAtLeast(1)))
                delayMs = (delayMs * backoffMultiplier).toLong().coerceAtMost(maxDelayMs)
            }
        }

        return ClientResult.Failure(lastError ?: ClientError.NetworkError(Exception("Unknown")))
    }

    companion object {
        private const val TAG = "BigType"
    }
}
