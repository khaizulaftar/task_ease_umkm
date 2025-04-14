package com.khaizul.task_ease_umkm.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khaizul.task_ease_umkm.data.local.entity.TaskEntity
import com.khaizul.task_ease_umkm.ui.components.*
import com.khaizul.task_ease_umkm.viewmodel.TaskViewModel
import java.text.SimpleDateFormat

import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onAddTask: () -> Unit,
    onTaskClick: (Int) -> Unit,
    viewModel: TaskViewModel
) {
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())
    val categories = listOf("All", "Keuangan", "Pemasaran", "Operasional", "Lainnya")
    val priorities = listOf("All", "High", "Medium", "Low")

    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPriority by remember { mutableStateOf("All") }
    var showCompleted by remember { mutableStateOf(false) }
    var filteredTasks by remember { mutableStateOf(tasks) }

    LaunchedEffect(tasks, selectedCategory, selectedPriority, showCompleted) {
        filteredTasks = tasks.filter { task ->
            (selectedCategory == "All" || task.category == selectedCategory) &&
                    (selectedPriority == "All" ||
                            (selectedPriority == "High" && task.priority == 1) ||
                            (selectedPriority == "Medium" && task.priority == 2) ||
                            (selectedPriority == "Low" && task.priority == 3)) &&
                    (if (showCompleted) task.isCompleted else !task.isCompleted) // Perubahan di sini
        }.sortedBy { it.dueDate }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TaskEase UMKM") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppDropdownMenu(
                    label = "Category",
                    options = categories,
                    selectedOption = selectedCategory,
                    onOptionSelected = { selectedCategory = it },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                AppDropdownMenu(
                    label = "Priority",
                    options = priorities,
                    selectedOption = selectedPriority,
                    onOptionSelected = { selectedPriority = it },
                    modifier = Modifier.weight(1f)
                )
            }

            // Show completed toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show completed:", modifier = Modifier.padding(end = 8.dp))
                Switch(
                    checked = showCompleted,
                    onCheckedChange = { showCompleted = it }
                )
            }

            // Task List
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tasks found", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskClick = { onTaskClick(task.id) },
                            onCompleteTask = { viewModel.updateTask(task.copy(isCompleted = true)) },
                            onDeleteTask = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: TaskEntity,
    onTaskClick: () -> Unit,
    onCompleteTask: () -> Unit,
    onDeleteTask: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        onClick = onTaskClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (task.priority) {
                1 -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                3 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = dateFormat.format(task.dueDate),
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (task.dueTime != null) {
                        Text(
                            text = timeFormat.format(task.dueTime),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(IntrinsicSize.Min) // Pastikan tinggi row sesuai konten
                ) {
                    IconButton(
                        onClick = { onDeleteTask() },
                        modifier = Modifier
                            .size(40.dp) // Ukuran lebih besar untuk touch target yang baik
                            .padding(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp)) // Jarak antara icon dan tombol

                    FilledTonalButton(
                        onClick = onCompleteTask,
                        enabled = !task.isCompleted,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(if (task.isCompleted) "✓" else "Complete")
                    }
                }
            }
        }
    }
}