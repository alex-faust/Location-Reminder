package com.udacity.project4.locationreminders.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

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

    private val remoteReminders = listOf(reminder1, reminder2).sortedBy { it.id }
    private val localReminders = listOf(reminder3).sortedBy { it.id }
    private val newReminder = listOf(reminder3).sortedBy { it.id }

    private lateinit var reminderDataSource: FakeDataSource
    private lateinit var remindersRepository: RemindersLocalRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createRepository() {
        reminderDataSource = FakeDataSource(localReminders.toMutableList())

        /*remindersRepository = RemindersLocalRepository(
            //reminderDataSource, Dispatchers.Unconfined
        )*/

    }

    /*@ExperimentalCoroutinesApi
    @Test
    fun get() = mainCoroutineRule.runTest {
        val remidners = remindersRepository.

    }*/



}