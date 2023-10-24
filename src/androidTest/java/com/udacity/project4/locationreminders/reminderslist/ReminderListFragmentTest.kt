package com.udacity.project4.locationreminders.reminderslist

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.local.RemindersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {
    private lateinit var remindersRepository: RemindersRepository

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    @Test
    fun reminderListScreen_clickOnFAB_opensSaveReminderFragment() {
        val activityScenario = ActivityScenario.launchActivityForResult(RemindersActivity::class.java)

        //onView(withId(R.id.))
    }

    @Test
    fun saveReminderFrag_clickOnLocationBtn_opensSelectLocationFrag() {
        val activityScenario = ActivityScenario.launchActivityForResult(RemindersActivity::class.java)


    }

    @Test
    fun saveReminderFrag_clickOnSaveBtn_opensReminderListActivity() {
        val activityScenario = ActivityScenario.launchActivityForResult(RemindersActivity::class.java)


    }


}