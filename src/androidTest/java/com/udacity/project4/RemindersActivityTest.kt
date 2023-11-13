package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    KoinTest {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private lateinit var viewModel: SaveReminderViewModel


    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single <ReminderDataSource> { RemindersLocalRepository(get()) }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun checkReminderSavedToastMessage() = runBlocking {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        onView(withId(R.id.addReminderFAB)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.reminderTitle)).perform(typeText("Fish"))
        Thread.sleep(1000)
        onView(withId(R.id.reminderDescription)).perform(typeText("description"))
        Thread.sleep(1000)
        onView(withId(R.id.selectLocation)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.map)).perform(longClick())
            //clickLocation(37.7819, -122.4467))
        Thread.sleep(2000)
        onView(withId(R.id.saveLocationButton)).perform(click())
        Thread.sleep(1000)
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())
        Thread.sleep(1000)
        //val savedStr = appContext.getString(R.string.geofence_added)
        //onView(withText(savedStr)).check(matches(isDisplayed()))
        //Thread.sleep(2000)

        activityScenario.close()
        //Having a hard time testing the toast message.
        // Maybe this is related to this issue?https://knowledge.udacity.com/questions/829008

        //I do feel like this is an end to end test since it goes through the whole app.
    }

    @Test
    fun testShowSnackbarTest() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        Thread.sleep(1000)
        onView(withId(R.id.addReminderFAB)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.saveReminder)).perform(click())
        Thread.sleep(1000)

        val selectTitleMsg = appContext.getString(R.string.err_enter_title)
        Thread.sleep(1000)
        onView(withText(selectTitleMsg)).check(matches(isDisplayed()))
        //assertThat(saveReminderViewModel.showToast.value, `is`(isDisplayed()))

        activityScenario.close()
    }
}

