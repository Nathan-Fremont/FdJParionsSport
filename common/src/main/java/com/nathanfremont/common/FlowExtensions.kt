package com.nathanfremont.common

import android.annotation.SuppressLint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response
import timber.log.Timber

fun flowDebugImplDefaultMessagePrefix(
    method: String,
    methodArgs: Array<out Pair<String, Any?>>,
    className: String,
    itemType: String? = null,
): String = method
    .plus("(${methodArgs.joinToString { it.first.plus('=').plus(it.second) }}): ")
    .plus(className)
    .plus(itemType?.run { "<$this>" }.orEmpty())
    .plus(" => ")

inline fun <reified T> Flow<T>.debugWith(
    tag: String,
    method: String,
    vararg methodArgs: Pair<String, Any?>,
    subscribeMessage: String? = flowDebugImplDefaultMessagePrefix(
        method = method,
        className = "Flow",
        methodArgs = methodArgs,
        itemType = T::class.java.simpleName
    ).plus("subscribed"),
    crossinline successFormatter: (value: T) -> String = flowDebugImplDefaultMessagePrefix(
        method = method,
        className = "Flow",
        methodArgs = methodArgs,
        itemType = T::class.java.simpleName
    ).plus("emitted ")::plus,
    crossinline completionFormatter: (Throwable?) -> String = flowDebugImplDefaultMessagePrefix(
        method = method,
        className = "Flow",
        methodArgs = methodArgs,
        itemType = T::class.java.simpleName
    ).plus("completion ")::plus,
    crossinline errorFormatter: (Throwable) -> String = flowDebugImplDefaultMessagePrefix(
        method = method,
        className = "Flow",
        methodArgs = methodArgs,
        itemType = T::class.java.simpleName
    ).plus("error ")::plus,
): (
    debugMethod: (tag: String, msg: String, e: Throwable?) -> Unit,
) -> Flow<T> = { debugMethod ->
    if (BuildConfig.IS_LOGGABLE) this
        .onStart { subscribeMessage?.run { debugMethod(tag, this, null) } }
        .onEach { debugMethod(tag, successFormatter(it), null) }
        .onCompletion { debugMethod(tag, completionFormatter(it), it) }
        .catch { debugMethod(tag, errorFormatter(it), it) }
    else this
}

@SuppressLint("TimberExceptionLogging")
inline fun <reified T> Flow<T>.logd(
    tag: String,
    method: String,
    vararg methodArgs: Pair<String, Any?>,
    subscribeMessage: String? = flowDebugImplDefaultMessagePrefix(
        method = method,
        methodArgs = methodArgs,
        className = "Flow",
        itemType = T::class.java.simpleName
    ).plus("subscribed"),
    crossinline successFormatter: (value: T) -> String = flowDebugImplDefaultMessagePrefix(
        method = method,
        methodArgs = methodArgs,
        className = "Flow",
        itemType = T::class.java.simpleName
    ).plus("emitted ")::plus,
    crossinline completionFormatter: (Throwable?) -> String = flowDebugImplDefaultMessagePrefix(
        method = method,
        className = "Flow",
        methodArgs = methodArgs,
        itemType = T::class.java.simpleName
    ).plus("completion ")::plus,
    crossinline errorFormatter: (Throwable) -> String = flowDebugImplDefaultMessagePrefix(
        method = method,
        methodArgs = methodArgs,
        className = "Flow",
        itemType = T::class.java.simpleName
    ).plus("error ")::plus,
    crossinline logStackTrace: (Throwable) -> Boolean = { true },
): Flow<T> = debugWith(
    tag = tag,
    method = method,
    methodArgs = methodArgs,
    subscribeMessage = subscribeMessage,
    successFormatter = successFormatter,
    completionFormatter = completionFormatter,
    errorFormatter = errorFormatter
)() { debugTag: String, message: String, e: Throwable? ->
    Timber
        .tag(debugTag)
        .d(e?.takeIf(logStackTrace), message)
}

inline fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> {
    return kotlinx.coroutines.flow.combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6
    ) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
        )
    }
}

inline fun <T1, T2, T3, T4, T5, T6, T7, R> combine7(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R,
): Flow<R> {
    return kotlinx.coroutines.flow.combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6,
        flow7
    ) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
        )
    }
}

data object CustomTimeoutException : Exception() {
    private fun readResolve(): Any = CustomTimeoutException
}

@Throws(CustomTimeoutException::class)
suspend inline fun <reified T> Flow<T>.withTimeout(
    timeoutMillis: Long,
    timeoutException: Exception = CustomTimeoutException,
): T = withTimeoutOrNull(timeMillis = timeoutMillis) {
    return@withTimeoutOrNull this@withTimeout.first()
} ?: throw timeoutException

data class CustomHttpException(
    val errorCode: Int,
) : Throwable()

inline fun <reified T> Flow<Response<T>>.retryOnHttpCodes(
    delayMs: Long = 0L,
    maxRetries: Long = 1L,
    httpCodesToRetry: Set<Int>,
): Flow<T?> = map { value: Response<T> ->
    if (httpCodesToRetry.contains(value.code())) {
        throw CustomHttpException(errorCode = value.code())
    }
    value.body()
}
    .retry(retries = maxRetries) { error ->
        (error is CustomHttpException).also { if (it) delay(delayMs) }
    }

inline fun <reified T> Flow<Response<T>>.retryOnInternalErrorServerHttpCodes(
    delayMs: Long = 0L,
    maxRetries: Long = 1L,
    httpCodesToRetry: Set<Int> = (500..599).toSet(),
): Flow<T?> = retryOnHttpCodes(
    delayMs = delayMs,
    maxRetries = maxRetries,
    httpCodesToRetry = httpCodesToRetry,
)