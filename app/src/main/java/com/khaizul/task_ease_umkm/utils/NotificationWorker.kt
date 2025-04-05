package com.khaizul.task_ease_umkm.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.khaizul.task_ease_umkm.R
import com.khaizul.task_ease_umkm.data.local.database.AppDatabase
import java.util.Date
import kotlinx.coroutines.flow.first


class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val currentDate = Date()

        // Get overdue tasks
        val overdueTasks = database.taskDao().getOverdueTasks(currentDate).first()


        if (overdueTasks.isNotEmpty()) {
            showNotification(
                context = applicationContext,
                title = "Anda memiliki ${overdueTasks.size} tugas yang sudah lewat deadline",
                message = "Segera selesaikan tugas Anda!"
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
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}