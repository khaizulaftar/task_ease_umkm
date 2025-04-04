package com.khaizul.task_ease_umkm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khaizul.task_ease_umkm.data.local.entity.TaskEntity
import com.khaizul.task_ease_umkm.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private val _task = MutableStateFlow<TaskEntity?>(null)
    val task = _task.asStateFlow()

    fun loadTask(taskId: Int) {
        viewModelScope.launch {
            _task.value = repository.getTaskById(taskId)
        }
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
}