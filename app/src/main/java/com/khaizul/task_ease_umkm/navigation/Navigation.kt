package com.khaizul.task_ease_umkm.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.khaizul.task_ease_umkm.ui.screen.TaskEditScreen
import com.khaizul.task_ease_umkm.ui.screen.TaskListScreen
import com.khaizul.task_ease_umkm.viewmodel.TaskEditViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.TaskList.route
    ) {
        composable(Screen.TaskList.route) {
            TaskListScreen(
                onAddTask = { navController.navigate(Screen.TaskEdit.route) },
                onTaskClick = { taskId ->
                    navController.navigate("${Screen.TaskEdit.route}/$taskId")
                }
            )
        }

        // Route tanpa argumen (untuk tambah task)
        composable(Screen.TaskEdit.route) {
            val viewModel = hiltViewModel<TaskEditViewModel>()
            TaskEditScreen(
                taskId = null,
                navController = navController,
                viewModel = viewModel
            )
        }

        // Route dengan argumen (untuk edit task)
        composable(
            route = "${Screen.TaskEdit.route}/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            val viewModel = hiltViewModel<TaskEditViewModel>()

            TaskEditScreen(
                taskId = if (taskId == -1) null else taskId,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

sealed class Screen(val route: String) {
    object TaskList : Screen("task_list")
    object TaskEdit : Screen("task_edit")
}
