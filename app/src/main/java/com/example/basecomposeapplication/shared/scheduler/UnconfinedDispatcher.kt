package com.example.basecomposeapplication.shared.scheduler

import com.example.basecomposeapplication.domain.scheduler.DispatchersProvider
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class UnconfinedDispatcher : DispatchersProvider {
    override fun dispatcher(): CoroutineContext {
        return Dispatchers.Unconfined
    }
}
