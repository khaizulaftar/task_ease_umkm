package com.khaizul.task_ease_umkm.di

import android.content.Context
import com.khaizul.task_ease_umkm.data.local.database.AppDatabase
import com.khaizul.task_ease_umkm.data.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase) = database.taskDao()

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao) = TaskRepository(taskDao)
}