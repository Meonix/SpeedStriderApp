package com.example.basecomposeapplication.data.remote

import com.example.basecomposeapplication.data.local.datastore.Pref
import com.example.basecomposeapplication.data.model.TestModel
import com.example.basecomposeapplication.network.api.Api
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class AppNetworkDataSource @Inject constructor(
    private val api: Api,
    private val pref: Pref
) : NetworkDataSource {

    // A mutex is used to ensure that reads and writes are thread-safe.
    private val accessMutex = Mutex()

    override suspend fun searchUserByName(name: String): TestModel = accessMutex.withLock {
        val response = api.searchUserByName(name)
        pref.setAccessToken(response.age.toString())
        return response
    }
}