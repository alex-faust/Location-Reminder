package com.udacity.project4.locationreminders.savereminder

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeAndroidTestRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
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
    var  mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersRepository: FakeAndroidTestRepository
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setupViewModel() {
        remindersRepository = FakeAndroidTestRepository()
        saveReminderViewModel = SaveReminderViewModel(remindersRepository)
    }

    @Test
    fun validateAndSaveReminder() = runTest {
        val reminder = ReminderDataItem(
            "Title",
            "Description",
            "Trader Joes",
            40.0, 40.0
        )
        saveReminderViewModel.validateAndSaveReminder(reminder)

        val value = remindersRepository.getReminder(reminder.id)

        assertThat(value, `is`(reminder.id))

        /*
        java.lang.AssertionError:
        Expected: is "428c156e-ea20-43f7-8d76-06214ec4fdff"
        but: was <Success(data=ReminderDTO(title=Title1, description=Description1,
        location=Trader Joes, latitude=40.0, longitude=40.0, id=428c156e-ea20-43f7-8d76-06214ec4fdff))>
        */
    }

    @Test
    fun onClearTest() = runTest {
        val reminder = ReminderDataItem(
            "Title",
            "Description",
            "Trader Joes",
            40.0, 40.0
        )
        saveReminderViewModel.validateAndSaveReminder(reminder)
        saveReminderViewModel.onClear()
        val value = remindersRepository.getReminder(reminder.id)

        assertThat(value, `is`(nullValue()))
        /*java.lang.AssertionError:
        Expected: is null
        but: was <ReminderDataItem(title=Title1, description=Description1,
        location=Trader Joes, latitude=40.0, longitude=40.0,
        id=dd9750b9-8586-4b63-9302-6f3b846875dc)>*/
    }

    @After
    fun tearDown() {
        stopKoin()
    }

}