package com.khaizul.task_ease_umkm.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.khaizul.task_ease_umkm.data.local.dao.TaskDao
import com.khaizul.task_ease_umkm.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    suspend fun insertTask(task: TaskEntity) {
        try {
            taskDao.insertTask(task.copy(isSynced = false))
            ioScope.launch {
                trySyncWithFirebase(task)
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error inserting task", e)
            throw e
        }
    }

    suspend fun updateTask(task: TaskEntity) {
        try {
            taskDao.updateTask(task.copy(isSynced = false))
            ioScope.launch {
                trySyncWithFirebase(task)
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error updating task", e)
            throw e
        }
    }

    suspend fun deleteTask(task: TaskEntity) {
        try {
            taskDao.deleteTask(task)
            if (task.isSynced) {
                ioScope.launch {
                    try {
                        deleteTaskFromFirebase(task.id)
                    } catch (e: Exception) {
                        Log.e("TaskRepository", "Background delete failed", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error deleting task", e)
            throw e
        }
    }

    suspend fun getTaskById(taskId: Int): TaskEntity? = taskDao.getTaskById(taskId)

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksByCategory(category: String): Flow<List<TaskEntity>> =
        taskDao.getTasksByCategory(category)

    fun getTasksByPriority(priority: Int): Flow<List<TaskEntity>> =
        taskDao.getTasksByPriority(priority)

    fun getOverdueTasks(currentDate: Date): Flow<List<TaskEntity>> =
        taskDao.getOverdueTasks(currentDate)

    private suspend fun trySyncWithFirebase(task: TaskEntity) {
        try {
            if (auth.currentUser != null) {
                syncTaskWithFirebase(task)
                taskDao.markTaskAsSynced(task.id)
            }
        } catch (e: Exception) {
            Log.w("TaskRepository", "Background sync failed for task ${task.id}", e)
        }
    }

    private suspend fun syncTaskWithFirebase(task: TaskEntity) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .collection("tasks").document(task.id.toString())
            .set(task.toFirestoreMap())
            .await()
    }

    private suspend fun deleteTaskFromFirebase(taskId: Int) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .collection("tasks").document(taskId.toString())
            .delete()
            .await()
    }
}

fun TaskEntity.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "category" to category,
        "priority" to priority,
        "dueDate" to dueDate.time,
        "dueHour" to dueHour,
        "dueMinute" to dueMinute,
        "isCompleted" to isCompleted,
        "createdAt" to createdAt.time,
        "isSynced" to isSynced
    )
}