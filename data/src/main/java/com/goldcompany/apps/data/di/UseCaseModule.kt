package com.goldcompany.apps.data.di

import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.data.usecase.GetTasksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Singleton
    @Provides
    fun provideGetTasksUseCase(
        repository: TaskRepository
    ): GetTasksUseCase = GetTasksUseCase(repository)
}