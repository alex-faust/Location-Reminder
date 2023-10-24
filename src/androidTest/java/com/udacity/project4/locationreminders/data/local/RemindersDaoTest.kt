package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() { //this database will be deleted once the process is killed. It is never stored.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminderAndGetById() = runTest {
        val reminder = ReminderDTO("Title",
            "Description",
            "White Castle",
            40.0, 40.0)
        database.reminderDao().saveReminder(reminder)

        val loaded = database.reminderDao().getReminderById(reminder.id)

        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id,`is`(reminder.id))
        assertThat(loaded.title,`is`(reminder.title))
        assertThat(loaded.description,`is`(reminder.description))
        assertThat(loaded.location,`is`(reminder.location))
        assertThat(loaded.longitude,`is`(reminder.longitude))
        assertThat(loaded.latitude,`is`(reminder.latitude))
    }

    @Test
    fun deleteAllReminders() = runTest {
        val reminder1 = ReminderDTO("Title",
            "Description",
            "White Castle",
            40.0, 40.0)
        val reminder2 = ReminderDTO("Title2",
            "Description2",
            "White Castle",
            40.0, 40.0)
        val reminder3 = ReminderDTO("Title3",
            "Description3",
            "White Castle",
            40.0, 40.0)
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        val reminders = database.reminderDao().getReminders()

        assertThat(reminders.size, `is`(3))

        database.reminderDao().deleteAllReminders()
        val reminders1 = database.reminderDao().getReminders()

        assertThat(reminders1.size, `is`(0))
    }
}
