package com.udacity.project4.locationreminders.reminderslist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeAndroidTestRepository
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersRepository: FakeAndroidTestRepository
    private lateinit var remindersListViewModel: RemindersListViewModel

    @Before
    fun setupViewModel() {
        remindersRepository = FakeAndroidTestRepository()
        val reminder1 = ReminderDTO(
            "Title1",
            "Description1",
            "Trader Joes",
            40.0, 40.0
        )
        val reminder2 = ReminderDTO(
            "Title1",
            "Description1",
            "Trader Joes",
            40.0, 40.0
        )
        val reminder3 = ReminderDTO(
            "Title1",
            "Description1",
            "Trader Joes",
            40.0, 40.0
        )
        remindersRepository.addReminders(reminder1, reminder2, reminder3)
        remindersListViewModel = RemindersListViewModel(remindersRepository)
        //remindersListViewModel.remindersList.value =
    }

    @Test
    fun addNewReminder_setsNewReminderEvent() {
        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(value, not(nullValue()))
    }

    @Test
    fun showNoDataTest() = runTest {
        remindersRepository.deleteAllReminders()
        remindersListViewModel.invalidateShowNoData()

        val value = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(value, `is`(nullValue()))
    }

    @After
    fun tearDown() {
        stopKoin()
    }

}