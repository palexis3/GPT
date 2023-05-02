package com.example.gpt.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable? = null) : Result<Nothing>
    object Loading : Result<Nothing>
}

// NOTE: This flow extension method is to help handle the loading, error and success
// states at the the view model layer when flows are being consumed.
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> {
            Result.Success(it)
        }
        .onStart {
            delay(100L)
            emit(Result.Loading)
        }
        .catch { emit(Result.Error(it)) }
}
