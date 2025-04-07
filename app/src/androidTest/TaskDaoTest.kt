import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import androidx.room.Room
import android.content.Context
import com.khaizul.task_ease_umkm.data.local.database.AppDatabase
import com.khaizul.task_ease_umkm.data.local.dao.TaskDao
import com.khaizul.task_ease_umkm.data.local.entity.TaskEntity

import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import java.util.Date
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`


@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTaskAndGetById() = runTest {
        val task = TaskEntity(
            title = "Test Task",
            description = "Test Description",
            category = "Test",
            dueDate = Date()
        )
        dao.insertTask(task)

        val loaded = dao.getTaskById(1)
        assertThat(loaded?.title, `is`("Test Task"))
    }

    // Add more tests...
}