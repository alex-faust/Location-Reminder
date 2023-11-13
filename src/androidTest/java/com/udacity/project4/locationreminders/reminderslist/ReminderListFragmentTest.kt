package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {
    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initRepository() {
        stopKoin()
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
            single { Room.inMemoryDatabaseBuilder(
                appContext, RemindersDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
                .reminderDao()
            }
            single { get<RemindersLocalRepository>() }

        }

        startKoin {
            modules(listOf(myModule))
        }
        repository = get()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun saveReminderToFragmentList() = runTest {
        val reminder1 = ReminderDTO("Title1",
            "Description1",
            "White Castle",
            40.0, 40.0)

        repository.saveReminder(reminder1)

        launchFragmentInContainer<ReminderListFragment>(themeResId= R.style.AppTheme)

        onView(withId(R.id.remindersRecyclerView)).check(matches(hasDescendant(withText(reminder1.title))))
        onView(withId(R.id.remindersRecyclerView)).check(matches(hasDescendant(withText(reminder1.description))))
        onView(withId(R.id.remindersRecyclerView)).check(matches(hasDescendant(withText(reminder1.location))))
    }

    @Test
    fun testNavigateToSaveLocation() = runTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(themeResId= R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
}