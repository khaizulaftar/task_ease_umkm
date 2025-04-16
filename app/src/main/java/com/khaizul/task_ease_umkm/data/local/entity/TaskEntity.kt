package com.khaizul.task_ease_umkm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val priority: Int = 2, // 1 = High, 2 = Medium, 3 = Low
    val dueDate: Date,
    val dueHour: Int = 0,
    val dueMinute: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val isSynced: Boolean = false
) {
    fun getFormattedTime(): String {
        return "%02d:%02d".format(dueHour, dueMinute)
    }

    fun hasTimeSet(): Boolean {
        return dueHour != 0 || dueMinute != 0
    }
}
