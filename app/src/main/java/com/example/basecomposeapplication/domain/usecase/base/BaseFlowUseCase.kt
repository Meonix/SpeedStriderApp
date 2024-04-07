package com.example.basecomposeapplication.domain.usecase.base

import com.example.basecomposeapplication.domain.interactor.inputport.BaseInput
import com.example.basecomposeapplication.domain.interactor.outputport.BaseObserver
import com.example.basecomposeapplication.shared.coroutines.ControlledRunner
import com.example.basecomposeapplication.shared.coroutines.handleErrors
import com.example.basecomposeapplication.shared.scheduler.IODispatcher
import com.example.basecomposeapplication.shared.scheduler.MainDispatcher
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * For flow UseCase.
 */

abstract class BaseFlowUseCase<in Input : BaseInput, Output>() {

    abstract suspend fun buildUseCase(input: Input): Flow<Output>

    private var controlledRunner = ControlledRunner<Unit>()

    suspend operator fun invoke(
        ioDispatcherProvider: IODispatcher,
        mainDispatcherProvider: MainDispatcher,
        input: Input,
        block: BaseObserver<out Output>.() -> Unit
    ) {
        controlledRunner.cancelPreviousThenRun {
            val response = BaseObserver<Output>().apply { block() }
            response()
            try {
                withContext(ioDispatcherProvider.dispatcher()) {
                    buildUseCase(input).handleErrors {
                        withContext(mainDispatcherProvider.dispatcher()) {
                            response(it)
                        }
                    }.collect {
                        withContext(mainDispatcherProvider.dispatcher()) {
                            response(it)
                        }
                    }
                }
            } catch (cancellationException: CancellationException) {
                response(cancellationException)
            } catch (throwable: Throwable) {
                response(throwable)
            }
        }
    }
}
