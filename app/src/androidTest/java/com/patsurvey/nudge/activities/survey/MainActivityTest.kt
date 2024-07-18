package com.patsurvey.nudge.activities.survey

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.MainActivityViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ConnectionMonitorV2
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


@LargeTest
@RunWith(androidx.test.runner.AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    @Mock
    private lateinit var prefRepo: PrefRepo

    @Mock
    private lateinit var apiInterface: ApiService

    @Mock
    private lateinit var casteListDao: CasteListDao

    @Mock
    private lateinit var tolaDao: TolaDao

    @Mock
    private lateinit var stepsListDao: StepsListDao

    @Mock
    private lateinit var villageListDao: VillageListDao

    @Mock
    private lateinit var didiDao: DidiDao

    @Mock
    private lateinit var answerDao: AnswerDao

    @Mock
    private lateinit var numericAnswerDao: NumericAnswerDao

    @Mock
    private lateinit var questionListDao: QuestionListDao

    @Mock
    private lateinit var trainingVideoDao: TrainingVideoDao

    @Mock
    private lateinit var bpcSummaryDao: BpcSummaryDao


    @Mock
    private lateinit var poorDidiListDao: PoorDidiListDao

    @Mock
    private lateinit var languageListDao: LanguageListDao

    @Mock
    private lateinit var connectionMonitor: ConnectionMonitorV2

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
//        viewModel = MainActivityViewModel(
//            prefRepo = prefRepo,
//            apiService = apiInterface,
//            casteListDao = casteListDao,
//            tolaDao = tolaDao,
//            didiDao = didiDao,
//            villegeListDao = villageListDao,
//            answerDao = answerDao,
//            numericAnswerDao = numericAnswerDao,
//            questionDao = questionListDao,
//            trainingVideoDao = trainingVideoDao,
//            bpcSummaryDao = bpcSummaryDao,
//            poorDidiListDao = poorDidiListDao,
//            stepsListDao = stepsListDao,
//            languageListDao = languageListDao,
//            connectionMonitor = connectionMonitor
//        )
        launchActivity<MainActivity>().use { scenario ->
            scenario.moveToState(Lifecycle.State.CREATED)
        }
    }

    @Test
    fun testIsLoggedInSuccess() {
        // Mock the behavior of prefRepo
        Mockito.`when`(prefRepo.getAccessToken()).thenReturn("your_access_token")

        val isLoggedIn = viewModel.isLoggedIn()

        assert(isLoggedIn)
    }

    @Test
    fun testIsLoggedInFailed() {
        // Mock the behavior of prefRepo
        Mockito.`when`(prefRepo.getAccessToken()).thenReturn("")

        val isLoggedIn = viewModel.isLoggedIn()

        assert(!isLoggedIn)
    }

}