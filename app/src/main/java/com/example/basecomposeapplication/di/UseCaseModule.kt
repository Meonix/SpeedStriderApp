package com.example.basecomposeapplication.di

import com.example.basecomposeapplication.data.repository.Repository
import com.example.basecomposeapplication.domain.usecase.DBUseCase
import com.example.basecomposeapplication.domain.usecase.test.TestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {
    @Provides
    fun provideTestUseCase(repository: Repository): TestUseCase {
        return TestUseCase(repository)
    }

    @Provides
    fun provideDBUseCase(repository: Repository): DBUseCase {
        return DBUseCase(repository)
    }
}