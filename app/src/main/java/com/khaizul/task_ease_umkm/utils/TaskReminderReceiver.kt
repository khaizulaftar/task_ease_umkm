package com.khaizul.task_ease_umkm.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Pengingat Tugas"
        val message =
            intent.getStringExtra("message") ?: "Anda memiliki tugas yang harus diselesaikan"
        NotificationHelper.showNotification(context, title, message)
    }
}