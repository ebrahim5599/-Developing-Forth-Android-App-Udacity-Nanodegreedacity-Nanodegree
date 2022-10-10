package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.rule.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)
    }

    @Test
    fun testShouldReturnError () = runBlockingTest  {
        fakeDataSource.setShouldReturnError(true)
        saveReminderFakeData()
        viewModel.loadReminders()
        MatcherAssert.assertThat(
            viewModel.showSnackBar.value, CoreMatchers.`is`("Reminders not found")
        )
    }

    @Test
    fun checkLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        saveReminderFakeData()
        viewModel.loadReminders()
        MatcherAssert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(false))
    }

    private suspend fun saveReminderFakeData() {
        fakeDataSource.saveReminder(
            ReminderDTO(
                "Title",
                "Description",
                "Location",
                30.033333,
                31.233334)
        )
    }
}