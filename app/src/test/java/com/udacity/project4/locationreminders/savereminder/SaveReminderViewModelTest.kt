package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.rule.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest: AutoCloseKoinTest(){

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)
    }

    @Test
    fun shouldReturnError () = runBlockingTest  {
        val result = viewModel.validateEnteredData(createIncompleteReminder())
        MatcherAssert.assertThat(result, CoreMatchers.`is`(false))
    }

    private fun createIncompleteReminder(): ReminderDataItem {
        return ReminderDataItem(
            "",
            "Description",
            "Location",
            30.033333,
            31.233334)
    }

    @Test
    fun checkLoading() = runBlockingTest {

        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(createFakeReminder())
        MatcherAssert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(false))
    }

    private fun createFakeReminder(): ReminderDataItem {
        return ReminderDataItem(
            "Title",
            "Description",
            "Location",
            30.033333,
            31.233334)

    }
}