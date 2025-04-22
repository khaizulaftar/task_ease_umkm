package com.khaizul.task_ease_umkm.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.khaizul.task_ease_umkm.ui.components.*
import com.khaizul.task_ease_umkm.viewmodel.TaskEditViewModel
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.khaizul.task_ease_umkm.data.local.entity.TaskEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: Int?, navController: NavController, viewModel: TaskEditViewModel
) {
    val task by viewModel.task.collectAsState()
    val categories = listOf("Keuangan", "Pemasaran", "Operasional", "Lainnya")

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories[0]) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var dueHour by remember { mutableIntStateOf(0) }
    var dueMinute by remember { mutableIntStateOf(0) }
    var priority by remember { mutableStateOf(2) }

    var isTitleError by remember { mutableStateOf(false) }
    var isDateError by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        taskId?.let { viewModel.loadTask(it) }
    }

    LaunchedEffect(task) {
        task?.let {
            title = it.title
            description = it.description
            category = it.category
            dueDate = it.dueDate.time
            dueHour = it.dueHour
            dueMinute = it.dueMinute
            priority = it.priority
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (taskId == null) "Tambahkan Tugas Baru" else "Edit Tugas") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Field
            AppTextField(
                label = "Judul",
                value = title,
                onValueChange = {
                    title = it
                    isTitleError = false
                },
                isError = isTitleError,
                errorMessage = "Title is required",
                modifier = Modifier.fillMaxWidth()
            )

            // Description Field
            AppTextField(
                label = "Keterangan",
                value = description,
                onValueChange = { description = it },
                singleLine = false,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            // Category Dropdown
            AppDropdownMenu(
                label = "Kategori",
                options = categories,
                selectedOption = category,
                onOptionSelected = { category = it },
                modifier = Modifier.fillMaxWidth()
            )

            // Due Date Picker
            AppDatePicker(
                label = "Tenggal", selectedDate = dueDate, onDateSelected = {
                    dueDate = it
                    isDateError = false
                }, modifier = Modifier.fillMaxWidth()
            )

            // Time Picker
            AppTimePicker(
                label = "Waktu",
                selectedHour = dueHour,
                selectedMinute = dueMinute,
                onTimeSelected = { hour, minute ->
                    dueHour = hour
                    dueMinute = minute
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Priority Section
            Text(
                text = "Prioritas",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PriorityChip(label = "Tinggi", selected = priority == 1, onClick = { priority = 1 })
                PriorityChip(label = "Sedang", selected = priority == 2, onClick = { priority = 2 })
                PriorityChip(label = "Rendah", selected = priority == 3, onClick = { priority = 3 })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            AppButton(
                text = if (taskId == null) "Buat Tugas" else "Perbarui Tugas", onClick = {
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
                        dueHour = dueHour,
                        dueMinute = dueMinute
                    ) ?: TaskEntity(
                        title = title,
                        description = description,
                        category = category,
                        priority = priority,
                        dueDate = Date(dueDate!!),
                        dueHour = dueHour,
                        dueMinute = dueMinute
                    )

                    if (taskId == null) {
                        viewModel.addTask(newTask)
                    } else {
                        viewModel.updateTask(newTask)
                    }
                    navController.popBackStack()
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            // Delete Button (only for existing tasks)
            if (taskId != null) {
                AppButton(
                    text = "Hapus Tugas", onClick = {
                        task?.let {
                            viewModel.deleteTask(it)
                            navController.popBackStack()
                        }
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }
        }
    }
}