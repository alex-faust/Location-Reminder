package com.udacity.project4.locationreminders.savereminder

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeTestRepository
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersRepository: FakeTestRepository
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setupViewModel() {
        remindersRepository = FakeTestRepository()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @Test
    fun validateAndSaveReminderShowLoading() = runTest {
        val reminder = ReminderDataItem(
            "Title",
            "Description",
            "Trader Joes",
            40.0, 40.0
        )
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(reminder)

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    //should return error snackbar
    @Test
    fun showSnackbarIfNoDataEntered() = runTest {
        val reminder = ReminderDataItem(
            null,
            "Description",
            "Trader Joes",
            40.0, 40.0
        )
        saveReminderViewModel.validateAndSaveReminder(reminder)
        mainCoroutineRule.pauseDispatcher()
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(2131886145))
    }

    @After
    fun tearDown() {
        stopKoin()
    }

}