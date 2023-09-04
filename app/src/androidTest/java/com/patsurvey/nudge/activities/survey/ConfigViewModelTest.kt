package com.patsurvey.nudge.activities.survey

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.splash.ConfigViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.BpcScorePercentageDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.ConfigResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ConfigViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    @Mock
    private lateinit var prefRepo: PrefRepo

    @Mock
    private lateinit var apiInterface: ApiService

    @Mock
    private lateinit var languageListDao: LanguageListDao

    @Mock
    private lateinit var casteListDao: CasteListDao

    @Mock
    private lateinit var bpcScorePercentageDao: BpcScorePercentageDao

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: ConfigViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = ConfigViewModel(prefRepo, apiInterface, languageListDao, casteListDao, bpcScorePercentageDao)
    }

    @Test
    fun testIsLoggedIn() {
        launchActivity<MainActivity>().use { scenario ->
            scenario.moveToState(Lifecycle.State.CREATED)
        }
        // Mock the behavior of prefRepo
        `when`(prefRepo.getAccessToken()).thenReturn("your_access_token")

        val isLoggedIn = viewModel.isLoggedIn()

        assert(isLoggedIn)
    }

    @Test
    fun testFetchLanguageDetailsSuccess() = testScope.runBlockingTest {
        // Mock API response
        val response: ApiResponseModel<ConfigResponseModel> = ApiResponseModel(SUCCESS, "Success", ConfigResponseModel(emptyList(), emptyList(), emptyList())) // Replace yourData with actual data
        `when`(apiInterface.configDetails()).thenReturn(response)

        val imageListLiveData = MutableLiveData<List<String>>()

        viewModel.fetchLanguageDetails(context) { imageList ->
            imageListLiveData.value = imageList
        }

        // Use LiveData observer or other means to verify the result
        // assert(imageListLiveData.value == expectedImageList)
    }

    @Test
    fun testFetchLanguageDetailsFailure() = testScope.runBlockingTest {
        // Mock API response for failure
        val response: ApiResponseModel<ConfigResponseModel> = ApiResponseModel(FAIL, "Failure", null)
        `when`(apiInterface.configDetails()).thenReturn(response)

        val imageListLiveData = MutableLiveData<List<String>>()

        viewModel.fetchLanguageDetails(context) { imageList ->
            imageListLiveData.value = imageList
        }

        // Use LiveData observer or other means to verify the result
        // assert(imageListLiveData.value == emptyList())
    }

    // Write similar tests for other methods in your class
}
