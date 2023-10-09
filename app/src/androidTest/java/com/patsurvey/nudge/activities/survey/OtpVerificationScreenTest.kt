package com.patsurvey.nudge.activities.survey

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.activities.ui.login.OtpVerificationRepository
import com.patsurvey.nudge.activities.ui.login.OtpVerificationScreen
import com.patsurvey.nudge.activities.ui.login.OtpVerificationViewModel
import com.patsurvey.nudge.activities.ui.theme.Nudge_Theme
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.OtpVerificationModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class OtpVerificationScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    @Mock
    private lateinit var prefRepo: PrefRepo

    @Mock
    private lateinit var apiInterface: ApiService

    @Mock
    private lateinit var villageListDao: VillageListDao

    private lateinit var viewModel: OtpVerificationViewModel
    @Mock
    private lateinit var otpVerificationRepository: OtpVerificationRepository

    @Before
    fun setupLoginNavHost() {
        MockitoAnnotations.openMocks(this)
        composeTestRule.setContent {
            viewModel = OtpVerificationViewModel(otpVerificationRepository)
            Nudge_Theme {
                OtpVerificationScreen(navController = rememberNavController(), viewModel = viewModel, modifier = Modifier, mobileNumber = "")
            }
        }
    }

    @Test
    fun otpVerificationNavHost() {

        composeTestRule.onNodeWithText("To End Ultra Poverty").assertExists()
        composeTestRule.onNodeWithText("Enter OTP").assertExists()
        composeTestRule.onNodeWithText("Submit").assertHasClickAction()

    }

    @Test
    fun testValidateOtpSuccess() = testScope.runBlockingTest {
        val mockRequestBody = OtpRequest("9999999999", "310522")

        val expectedResponse: ApiResponseModel<OtpVerificationModel> = ApiResponseModel(
            SUCCESS,
            "SUCCESS",
            OtpVerificationModel("token")
        )

        Mockito.`when`(apiInterface.validateOtp(mockRequestBody)).thenReturn(expectedResponse)

        val actualResponse = apiInterface.validateOtp(mockRequestBody)
        assert(actualResponse.status == SUCCESS)
        assert(actualResponse.message == "SUCCESS")
        assert(actualResponse.data?.token != null)
        assert(actualResponse.data?.token != BLANK_STRING)

    }

    @Test
    fun testValidateOtpFailedDueToInvalidNumber() = testScope.runBlockingTest {
        val mockRequestBody = OtpRequest("9999999999", "310521")

        val expectedResponse: ApiResponseModel<OtpVerificationModel> = ApiResponseModel(
            FAIL,
            "OTP is invalid",
            null
        )

        Mockito.`when`(apiInterface.validateOtp(mockRequestBody)).thenReturn(expectedResponse)

        val actualResponse = apiInterface.validateOtp(mockRequestBody)
        assert(actualResponse.status == FAIL)
        assert(actualResponse.message == "OTP is invalid")
        assert(actualResponse.data == null)

    }

}