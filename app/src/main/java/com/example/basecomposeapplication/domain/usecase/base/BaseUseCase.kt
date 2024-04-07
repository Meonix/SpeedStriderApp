package com.example.basecomposeapplication.domain.usecase.base

import com.example.basecomposeapplication.domain.interactor.inputport.BaseInput
import com.example.basecomposeapplication.domain.interactor.outputport.BaseObserver
import com.example.basecomposeapplication.shared.coroutines.ControlledRunner
import com.example.basecomposeapplication.shared.scheduler.IODispatcher
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

/**
 * For Oneshot UseCase
 */
abstract class BaseUseCase<in Input : BaseInput, out Output> {
    abstract suspend fun buildUseCase(input: Input): Output

    private var controlledRunner = ControlledRunner<Unit>()

    suspend operator fun invoke(
        ioDispatcherProvider: IODispatcher,
        input: Input,
        block: suspend BaseObserver<out Output>.() -> Unit
    ) {
        controlledRunner.cancelPreviousThenRun {
            val response =
                BaseObserver<Output>().apply { block() } //apply functions like onSubscribe, onSuccess, onError ...
            response() //invoke onSubscribe
            try {
                val result =
                    withContext(ioDispatcherProvider.dispatcher()) { // async await response
                        buildUseCase(input)
                    }
                response(result) //invoke onSuccess
            } catch (cancellationException: CancellationException) {
                response(cancellationException) //invoke onCancel
            } catch (throwable: Throwable) {
                response(throwable) //invoke onError
            }
        }
    }
}
