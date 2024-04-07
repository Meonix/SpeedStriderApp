package com.example.basecomposeapplication.shared.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.handleErrors(error: suspend (Throwable) -> Unit): Flow<T> = catch { e -> error(e) }
