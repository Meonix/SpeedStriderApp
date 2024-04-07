package com.example.basecomposeapplication.domain.error

interface ErrorMapper {
    fun map(throwable: Throwable): ErrorEntity
}
