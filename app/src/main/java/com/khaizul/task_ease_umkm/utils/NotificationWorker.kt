package com.khaizul.task_ease_umkm.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.khaizul.task_ease_umkm.data.local.database.AppDatabase
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import com.khaizul.task_ease_umkm.R
import java.util.*

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val now = Calendar.getInstance()
        val currentDate = Date(now.timeInMillis)

        // Reset time components for date comparison
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.MILLISECOND, 0)
        val dateOnly = Date(now.timeInMillis)

        // Get current time for time-specific tasks
        val currentTime = Calendar.getInstance().run {
            set(Calendar.YEAR, 1970)
            set(Calendar.MONTH, 0)
            set(Calendar.DAY_OF_MONTH, 1)
            Date(timeInMillis)
        }

        // Get overdue tasks (past due date)
        val overdueTasks = database.taskDao().getOverdueTasks(dateOnly).first()

        // Get due today tasks with specific time that's passed
        val timeDueTasks = database.taskDao().getDueTasks(dateOnly, currentTime).first()

        // Combine and show notification
        val allDueTasks = overdueTasks + timeDueTasks
        if (allDueTasks.isNotEmpty()) {
            val taskWord = if (allDueTasks.size == 1) "tugas" else "tugas-tugas"
            val message = buildString {
                append("Tugas yang perlu perhatian:\n")
                allDueTasks.take(3).forEach { task ->
                    append("• ${task.title}")
                    if (task.dueTime != null) {
                        append(" (${SimpleDateFormat("HH:mm", Locale.getDefault()).format(task.dueTime)})")
                    }
                    append("\n")
                }
                if (allDueTasks.size > 3) {
                    append("...dan ${allDueTasks.size - 3} lebih")
                }
            }

            showNotification(
                context = applicationContext,
                title = "Anda memiliki ${allDueTasks.size} $taskWord yang perlu diselesaikan",
                message = message
            )
        }

        return Result.success()
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_channel",
                "Task Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "task_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}