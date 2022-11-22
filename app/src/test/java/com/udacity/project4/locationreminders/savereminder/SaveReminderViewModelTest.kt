package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.rule.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
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

    @After
    fun tearDown() {
        stopKoin()
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
    fun update_snackBar_empty_name_input () = mainCoroutineRule.runBlockingTest {
        // GIVEN
        var reminder = ReminderDataItem("", "description",
            "location", 6.454202, 3.599068)

        // WHEN
        Truth.assertThat(viewModel.validateEnteredData(reminder)).isFalse()
        Truth.assertThat(viewModel.showSnackBarInt.value).isEqualTo(R.string.err_enter_title)

    }


    @Test
    fun update_snackBar_empty_location_input () = mainCoroutineRule.runBlockingTest {

        // GIVEN
        var reminder = ReminderDataItem("Title", "description",
            "", 6.454202, 3.599068)

        Truth.assertThat(viewModel.validateEnteredData(reminder)).isFalse()
        Truth.assertThat(viewModel.showSnackBarInt.value).isEqualTo(R.string.err_select_location)

    }

    @Test
    fun checkLoading() = runBlockingTest {

        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(createFakeReminder())
        assertThat(viewModel.showLoading.value, CoreMatchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.value, CoreMatchers.`is`(false))
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