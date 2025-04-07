import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.android.testing.HiltAndroidRule
import androidx.hilt.android.testing.HiltAndroidTest
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.khaizul.task_ease_umkm.ui.screen.TaskListScreen
import com.khaizul.task_ease_umkm.ui.theme.TaskEaseTheme
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class TaskListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun displayTasks() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        composeTestRule.setContent {
            TaskEaseTheme {
                TaskListScreen(
                    onAddTask = {},
                    onTaskClick = {},
                    viewModel = hiltViewModel()
                )
            }
        }

        // Add assertions...
    }
}