package com.patsurvey.nudge.activities.ui.login

import androidx.compose.ui.text.input.TextFieldValue
import com.nhaarman.mockitokotlin2.whenever
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LoginViewModelTest {

    @Mock
    lateinit var loginRepository: LoginRepository
    lateinit var loginViewModel: LoginViewModel
    @Mock
    private lateinit var apiInterface: ApiService
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginViewModel = LoginViewModel(loginRepository)
    }

    @Test
    fun generateOtpSuccess(){
         val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        loginViewModel.mobileNumber.value= TextFieldValue("1234567890")
        runBlockingTest(testDispatcher){
            val mockCollection = ApiResponseModel<String>(SUCCESS,"OTP sent to 99*****999 mobile number","1234567890","")
            whenever(loginRepository.generateOtp("1234567890")).thenReturn(mockCollection)
            loginViewModel.generateOtp { success, message ->  }
        }
    }
    @Test
    fun generateOtpFail(){
         val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        loginViewModel.mobileNumber.value= TextFieldValue("1234567890")
        runBlockingTest(testDispatcher){
            val mockCollection = ApiResponseModel<String>(FAIL,"OTP sent to 99*****999 mobile number",null,"")
            whenever(loginRepository.generateOtp("1234567890")).thenReturn(mockCollection)
            loginViewModel.generateOtp { success, message ->  }
        }
    }
    @After
    fun tearDown() {

    }
}