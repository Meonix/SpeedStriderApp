package com.example.basecomposeapplication.data.remote

import com.example.basecomposeapplication.data.model.TestModel

interface NetworkDataSource {

    suspend fun searchUserByName(name: String): TestModel
}