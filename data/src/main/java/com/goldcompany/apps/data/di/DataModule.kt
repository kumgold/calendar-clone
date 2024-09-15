package com.goldcompany.apps.data.di

import android.content.Context
import com.goldcompany.apps.data.db.todo.TodoDao
import com.goldcompany.apps.data.db.AppDatabase
import com.goldcompany.apps.data.db.schedule.ScheduleDao
import com.goldcompany.apps.data.repository.ScheduleRepository
import com.goldcompany.apps.data.repository.TodoRepository
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
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideTodoDao(database: AppDatabase): TodoDao = database.todoDao()

    @Provides
    fun provideScheduleDao(database: AppDatabase): ScheduleDao = database.scheduleDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideTaskRepository(
        todoDao: TodoDao
    ): TodoRepository = TodoRepository(todoDao)

    @Singleton
    @Provides
    fun provideScheduleRepository(
        scheduleDao: ScheduleDao
    ): ScheduleRepository = ScheduleRepository(scheduleDao)
}