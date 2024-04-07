package com.example.basecomposeapplication.domain.usecase.base

import com.example.basecomposeapplication.domain.interactor.inputport.BaseInput

/**
 * For using directly on current thread, not switching.
 */
abstract class BaseDirectUseCase<in Input : BaseInput, Output> {
    abstract fun buildUseCase(input: Input): Output

    operator fun invoke(input: Input): Output {
        return buildUseCase(input)
    }
}
