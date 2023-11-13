package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

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



    @Before
    fun createRepository() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
        remindersLocalRepository = RemindersLocalRepository(remindersDatabase.reminderDao())

    }
    @After
    fun tearDown() {
        remindersDatabase.close()
    }

    @Test
    fun createAndSaveReminder() = runTest {
        remindersLocalRepository.saveReminder(reminder1)
        val result = remindersLocalRepository.getReminder(reminder1.id) as Result.Success

        val loaded = result.data
        assertThat(loaded.id, `is`(reminder1.id))

    }

    @Test
    fun errorWhenReminderDoesntExist() = runTest {
        val expectedError = remindersLocalRepository.getReminder("111")
        val error = (expectedError is Result.Error)
        assertThat(error, `is`(true))
    }



}