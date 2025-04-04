package com.khaizul.task_ease_umkm.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationHelper {
    private const val WORK_NAME = "task_notification_work"

    fun scheduleDailyNotification(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            24, // Repeat every 24 hours
            TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}