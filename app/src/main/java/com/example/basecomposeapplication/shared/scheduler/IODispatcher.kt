package com.example.basecomposeapplication.shared.scheduler

import com.example.basecomposeapplication.domain.scheduler.DispatchersProvider
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class IODispatcher : DispatchersProvider {
    override fun dispatcher(): CoroutineContext {
        return Dispatchers.IO
    }
}
