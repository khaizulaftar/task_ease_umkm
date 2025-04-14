package com.khaizul.task_ease_umkm.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.khaizul.task_ease_umkm.data.local.entity.TaskEntity
import com.khaizul.task_ease_umkm.ui.components.*
import com.khaizul.task_ease_umkm.viewmodel.TaskEditViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: Int?,
    navController: NavController,
    viewModel: TaskEditViewModel
) {
    val task by viewModel.task.collectAsState()
    val categories = listOf("Keuangan", "Pemasaran", "Operasional", "Lainnya")

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories[0]) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var dueTime by remember { mutableStateOf<Long?>(null) }
    var priority by remember { mutableStateOf(2) }

    var isTitleError by remember { mutableStateOf(false) }
    var isDateError by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        taskId?.let {
            viewModel.loadTask(it)
        }
    }

    LaunchedEffect(task) {
        task?.let {
            title = it.title
            description = it.description
            category = it.category
            dueDate = it.dueDate.time
            dueTime = it.dueTime?.time
            priority = it.priority
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Add Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppTextField(
                label = "Title",
                value = title,
                onValueChange = {
                    title = it
                    isTitleError = false
                },
                isError = isTitleError,
                errorMessage = "Title cannot be empty"
            )

            AppTextField(
                label = "Description",
                value = description,
                onValueChange = { description = it },
                singleLine = false,
                maxLines = 5
            )

            AppDropdownMenu(
                label = "Category",
                options = categories,
                selectedOption = category,
                onOptionSelected = { category = it }
            )

            AppDatePicker(
                label = "Due Date",
                selectedDate = dueDate,
                onDateSelected = {
                    dueDate = it
                    isDateError = false
                }
            )

            AppTimePicker(
                label = "Due Time (optional)",
                selectedTime = dueTime,
                onTimeSelected = { dueTime = it }
            )

            Text("Priority", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PriorityChip(
                    label = "High",
                    selected = priority == 1,
                    onClick = { priority = 1 }
                )
                PriorityChip(
                    label = "Medium",
                    selected = priority == 2,
                    onClick = { priority = 2 }
                )
                PriorityChip(
                    label = "Low",
                    selected = priority == 3,
                    onClick = { priority = 3 }
                )
            }

            AppButton(
                text = if (taskId == null) "Add Task" else "Update Task",
                onClick = {
                    if (title.isBlank()) {
                        isTitleError = true
                        return@AppButton
                    }

                    if (dueDate == null) {
                        isDateError = true
                        return@AppButton
                    }

                    val newTask = task?.copy(
                        title = title,
                        description = description,
                        category = category,
                        priority = priority,
                        dueDate = Date(dueDate!!),
                        dueTime = dueTime?.let { Date(it) }
                    ) ?: TaskEntity(
                        title = title,
                        description = description,
                        category = category,
                        priority = priority,
                        dueDate = Date(dueDate!!),
                        dueTime = dueTime?.let { Date(it) }
                    )

                    if (taskId == null) {
                        viewModel.addTask(newTask)
                    } else {
                        viewModel.updateTask(newTask)
                    }

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (taskId != null) {
                Spacer(modifier = Modifier.height(8.dp))
                AppButton(
                    text = "Delete Task",
                    onClick = {
                        task?.let {
                            viewModel.deleteTask(it)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }
        }
    }
}