package com.khaizul.task_ease_umkm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.khaizul.task_ease_umkm.navigation.AppNavigation
import com.khaizul.task_ease_umkm.ui.theme.TaskEaseUMKMTheme
import com.khaizul.task_ease_umkm.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Schedule daily notifications
        NotificationHelper.scheduleDailyNotification(this)

        setContent {
            TaskEaseUMKMTheme {
                // Get the navController
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}