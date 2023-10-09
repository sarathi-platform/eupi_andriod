package com.patsurvey.nudge.activities.survey

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.activities.ui.login.LoginRepository
import com.patsurvey.nudge.activities.ui.login.LoginScreen
import com.patsurvey.nudge.activities.ui.login.LoginViewModel
import com.patsurvey.nudge.activities.ui.theme.Nudge_Theme
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class LoginScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    @Mock
    private lateinit var prefRepo: PrefRepo

    @Mock
    private lateinit var apiInterface: ApiService

    private lateinit var viewModel: LoginViewModel

    @Mock
    private lateinit var  loginRepository: LoginRepository

    @Before
    fun setupLoginNavHost() {
        MockitoAnnotations.openMocks(this)
        composeTestRule.setContent {
            viewModel = LoginViewModel(loginRepository)
            Nudge_Theme {
                LoginScreen(navController = rememberNavController(), viewModel = viewModel, modifier = Modifier)
            }
        }
    }

    @Test
    fun loginNavHost() {

        composeTestRule.onNodeWithText("Enter Mobile").assertExists()
        composeTestRule.onNodeWithText("+91 - ").assertExists()
        composeTestRule.onNodeWithText("OTP will be sent to this number").assertExists()
        composeTestRule.onNodeWithText("Get OTP").assertExists()
        composeTestRule.onNodeWithText("Get OTP").assertHasClickAction()

    }

    @Test
    fun testGenerateOtpSuccess() = testScope.runBlockingTest {
        val mockRequestBody = LoginRequest("9999999999")

        val expectedResponse: ApiResponseModel<String> = ApiResponseModel(
            SUCCESS,
            "99*****999"
        )

        `when`(apiInterface.generateOtp(mockRequestBody)).thenReturn(expectedResponse)

        val actualResponse = apiInterface.generateOtp(mockRequestBody)
        assert(actualResponse.status == SUCCESS)
        assert(actualResponse.message == "OTP sent to 99*****999 mobile number")
        assert(actualResponse.data != null)

    }

    @Test
    fun testGenerateOtpFailedDueToInvalidNumber() = testScope.runBlockingTest {
        val mockRequestBody = LoginRequest("999999999")

        val expectedResponse: ApiResponseModel<String> = ApiResponseModel(
            FAIL,
            "Invalid Mobile number",
            null
        )

        `when`(apiInterface.generateOtp(mockRequestBody)).thenReturn(expectedResponse)

        val actualResponse = apiInterface.generateOtp(mockRequestBody)
        assert(actualResponse.status == FAIL)
        assert(actualResponse.message == "Invalid Mobile number")
        assert(actualResponse.data == null)

    }

    @Test
    fun testGenerateOtpFailedDueToUnRegisteredNumber() = testScope.runBlockingTest {
        val mockRequestBody = LoginRequest("999999999")

        val expectedResponse: ApiResponseModel<String> = ApiResponseModel(
            FAIL,
            "Fail",
            null
        )

        `when`(apiInterface.generateOtp(mockRequestBody)).thenReturn(expectedResponse)

        val actualResponse = apiInterface.generateOtp(mockRequestBody)
        assert(actualResponse.status == FAIL)
        assert(actualResponse.message == "Fail")
        assert(actualResponse.data == null)

    }
}