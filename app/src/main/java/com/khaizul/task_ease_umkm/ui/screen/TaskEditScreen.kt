package com.khaizul.task_ease_umkm.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.khaizul.task_ease_umkm.R
import com.khaizul.task_ease_umkm.ui.components.AppButton
import com.khaizul.task_ease_umkm.ui.components.AppDatePicker
import com.khaizul.task_ease_umkm.ui.components.AppDropdownMenu
import com.khaizul.task_ease_umkm.ui.components.AppTextField
import com.khaizul.task_ease_umkm.viewmodel.TaskEditViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: Int?,
    navController: NavController,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val task by viewModel.task.collectAsState()
    val categories = listOf("Keuangan", "Pemasaran", "Operasional", "Lainnya")

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories[0]) }
    var dueDate by remember { mutableStateOf<Long?>(null) }

    var isTitleError by remember { mutableStateOf(false) }
    var isDateError by remember { mutableStateOf(false) }

    // Load task if editing
    LaunchedEffect(taskId) {
        taskId?.let {
            viewModel.loadTask(it)
        }
    }

    // Update fields when task is loaded
    LaunchedEffect(task) {
        task?.let {
            title = it.title
            description = it.description
            category = it.category
            dueDate = it.dueDate.time
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
                        dueDate = Date(dueDate!!)
                    ) ?: TaskEntity(
                        title = title,
                        description = description,
                        category = category,
                        dueDate = Date(dueDate!!)
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
        }
    }
}