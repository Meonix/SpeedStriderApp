package com.example.basecomposeapplication.shared.coroutines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.handleErrors(error: suspend (Throwable) -> Unit): Flow<T> = catch { e -> error(e) }

@Composable
fun rememberStableCoroutineScope(): StableCoroutineScope {
    val scope = rememberCoroutineScope()
    return remember { StableCoroutineScope(scope) }
}

/** @see rememberStableCoroutineScope */
@Stable
class StableCoroutineScope(scope: CoroutineScope) : CoroutineScope by scope
