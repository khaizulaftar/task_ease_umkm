package com.khaizul.task_ease_umkm.data.repository

import com.khaizul.task_ease_umkm.data.local.dao.TaskDao
import com.khaizul.task_ease_umkm.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun getTaskById(taskId: Int): TaskEntity? = taskDao.getTaskById(taskId)

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksByCategory(category: String): Flow<List<TaskEntity>> =
        taskDao.getTasksByCategory(category)

    fun getOverdueTasks(currentDate: Date): Flow<List<TaskEntity>> =
        taskDao.getOverdueTasks(currentDate)
}