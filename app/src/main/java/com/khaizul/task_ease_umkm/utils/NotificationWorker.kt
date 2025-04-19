package com.khaizul.task_ease_umkm.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.khaizul.task_ease_umkm.data.local.database.AppDatabase
import kotlinx.coroutines.flow.first
import java.util.*

class NotificationWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val currentDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val overdueTasks = database.taskDao().getAllTasks().first()
                .filter { !it.isCompleted && it.dueDate.before(currentDate) }

            if (overdueTasks.isNotEmpty()) {
                NotificationHelper.showNotification(
                    context = applicationContext,
                    title = "Tugas Tertunda",
                    message = "Anda memiliki ${overdueTasks.size} tugas yang belum diselesaikan"
                )
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}