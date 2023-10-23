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
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
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
    fun saveReminderThenGetRemindersTest() {
        val reminder1 = ReminderDTO("Title1",
            "Description1",
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
    }
    @Test
    fun saveReminderAndGetById() = runTest {
        val reminder = ReminderDTO("Title",
            "Description",
            "White Castle",
            40.0, 40.0)
        database.reminderDao().saveReminder(reminder)

        val loaded = database.reminderDao().getReminderById(reminder.id)

        MatcherAssert.assertThat(loaded as ReminderDTO, notNullValue())
        MatcherAssert.assertThat(loaded.id,`is`(reminder.id))
        MatcherAssert.assertThat(loaded.title,`is`(reminder.title))
        MatcherAssert.assertThat(loaded.description,`is`(reminder.description))
        MatcherAssert.assertThat(loaded.location,`is`(reminder.location))
        MatcherAssert.assertThat(loaded.longitude,`is`(reminder.longitude))
        MatcherAssert.assertThat(loaded.latitude,`is`(reminder.latitude))
    }
}
