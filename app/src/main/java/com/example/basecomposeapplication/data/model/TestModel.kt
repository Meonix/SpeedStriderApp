package com.example.basecomposeapplication.data.model

import com.google.gson.annotations.SerializedName

data class TestModel(
    @SerializedName("name")
    val name: String,
    @SerializedName("age")
    val age: Int,
    @SerializedName("count")
    val count: Int,
)
