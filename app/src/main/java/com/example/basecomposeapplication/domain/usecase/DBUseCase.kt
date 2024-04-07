package com.example.basecomposeapplication.domain.usecase

import com.example.basecomposeapplication.data.repository.Repository
import com.example.basecomposeapplication.domain.interactor.inputport.BaseInput
import com.example.basecomposeapplication.domain.usecase.base.BaseUseCase

class DBUseCase(private val repository: Repository) :
    BaseUseCase<DBUseCase.Input, Unit>() {

    override suspend fun buildUseCase(input: Input) {
        return repository.setNote(input.content, input.time)
    }

    data class Input(val content: String, val time: String) : BaseInput()
}