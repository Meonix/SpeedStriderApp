package com.example.basecomposeapplication.domain.usecase.test

import com.example.basecomposeapplication.data.model.TestModel
import com.example.basecomposeapplication.data.repository.Repository
import com.example.basecomposeapplication.domain.interactor.inputport.BaseInput
import com.example.basecomposeapplication.domain.usecase.base.BaseUseCase

class TestUseCase(private val repository: Repository) :
    BaseUseCase<TestUseCase.Input, TestModel>() {

    override suspend fun buildUseCase(input: Input): TestModel {
        return repository.getMessageList(input.name)
    }

    data class Input(val name: String) : BaseInput()
}