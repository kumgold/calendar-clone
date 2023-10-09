package com.goldcompany.apps.data.di

import android.content.Context
import androidx.room.Room
import com.goldcompany.apps.data.db.TaskDao
import com.goldcompany.apps.data.db.TodoDatabase
import com.goldcompany.apps.data.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): TodoDatabase {
        return TodoDatabase.getInstance(context)
    }

    @Provides
    fun provideTaskDao(database: TodoDatabase): TaskDao = database.taskDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideTaskRepository(
        taskDao: TaskDao
    ): TaskRepository = TaskRepository(taskDao)
}