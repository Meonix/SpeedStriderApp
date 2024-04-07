package com.example.basecomposeapplication.network.api

import com.example.basecomposeapplication.data.model.TestModel
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("/")
    suspend fun searchUserByName(
        @Query("name") userName: String
    ): TestModel
}