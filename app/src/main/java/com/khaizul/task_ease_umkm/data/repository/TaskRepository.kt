package com.khaizul.task_ease_umkm.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.khaizul.task_ease_umkm.data.local.dao.TaskDao
import com.khaizul.task_ease_umkm.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
        syncTaskWithFirebase(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
        syncTaskWithFirebase(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
        deleteTaskFromFirebase(task.id)
    }

    suspend fun getTaskById(taskId: Int): TaskEntity? = taskDao.getTaskById(taskId)

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksByCategory(category: String): Flow<List<TaskEntity>> =
        taskDao.getTasksByCategory(category)

    fun getOverdueTasks(currentDate: Date): Flow<List<TaskEntity>> =
        taskDao.getOverdueTasks(currentDate)

    private suspend fun syncTaskWithFirebase(task: TaskEntity) {
        val userId = auth.currentUser?.uid ?: return

        try {
            firestore.collection("users").document(userId)
                .collection("tasks").document(task.id.toString())
                .set(task.toFirestoreMap())
                .await()

            taskDao.markTaskAsSynced(task.id)
        } catch (e: Exception) {
            // Optional: log or handle error
        }
    }

    private suspend fun deleteTaskFromFirebase(taskId: Int) {
        val userId = auth.currentUser?.uid ?: return

        try {
            firestore.collection("users").document(userId)
                .collection("tasks").document(taskId.toString())
                .delete()
                .await()
        } catch (e: Exception) {
            // Optional: log or handle error
        }
    }
}

fun TaskEntity.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "category" to category,
        "dueDate" to dueDate.time,
        "isCompleted" to isCompleted,
        "createdAt" to createdAt.time
    )
}
