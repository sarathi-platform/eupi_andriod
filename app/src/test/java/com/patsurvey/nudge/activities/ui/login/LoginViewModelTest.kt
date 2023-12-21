package com.patsurvey.nudge.activities.ui.login

import androidx.compose.ui.text.input.TextFieldValue
import com.nhaarman.mockitokotlin2.whenever
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LoginViewModelTest {

    @Mock
    lateinit var loginRepository: LoginRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginViewModel = LoginViewModel(loginRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun generateOtpSuccess() {
        loginViewModel.mobileNumber.value = TextFieldValue("1234567890")
        runTest {
            val mockCollection = ApiResponseModel<String>(
                SUCCESS, "OTP sent to 99*****999 mobile number", "1234567890", ""
            )
            whenever(loginRepository.generateOtp("1234567890")).thenReturn(mockCollection)
            loginViewModel.generateOtp { _, _ -> }
            assert(mockCollection.status == SUCCESS)
            assert(mockCollection.data != null)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun generateOtpFail() {
        loginViewModel.mobileNumber.value = TextFieldValue("1234567890")
        runTest {
            val mockCollection =
                ApiResponseModel<String>(FAIL, "OTP sent to 99*****999 mobile number", null, "")
            whenever(loginRepository.generateOtp("1234567890")).thenReturn(mockCollection)
            loginViewModel.generateOtp { _, _ -> }
            assert(mockCollection.status == FAIL)
            assert(mockCollection.data == null)
        }
    }

    @After
    fun tearDown() {

    }
}