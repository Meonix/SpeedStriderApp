package com.example.basecomposeapplication.domain.scheduler

import kotlin.coroutines.CoroutineContext

interface DispatchersProvider {
    fun dispatcher(): CoroutineContext
}
