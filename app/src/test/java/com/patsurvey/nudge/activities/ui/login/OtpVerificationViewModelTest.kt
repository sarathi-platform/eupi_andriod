package com.patsurvey.nudge.activities.ui.login

import com.nhaarman.mockitokotlin2.whenever
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.OtpVerificationModel
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class OtpVerificationViewModelTest {

    @Mock
    lateinit var otpVerificationRepository: OtpVerificationRepository
    private lateinit var otpVerificationViewModel: OtpVerificationViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        otpVerificationViewModel = OtpVerificationViewModel(otpVerificationRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun validateOtpSuccess() {
        otpVerificationViewModel.otpNumber.value = "123456"
        runTest {
            val mockCollection = ApiResponseModel<OtpVerificationModel>(
                SUCCESS,
                "",
                OtpVerificationModel("123456"),
                ""
            )
            whenever(otpVerificationRepository.validateOtp("123456")).thenReturn(mockCollection)
            otpVerificationViewModel.validateOtp { _, _ -> }
            assert(mockCollection.status == SUCCESS)
            assert(mockCollection.data != null)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun resendOtpSuccess() {
        otpVerificationViewModel.otpNumber.value = "123456"
        runTest {
            val mockCollection = ApiResponseModel(SUCCESS, "", "123456", "")
            whenever(otpVerificationRepository.generateOtp()).thenReturn(mockCollection)
            otpVerificationViewModel.resendOtp { _, _ -> }
            assert(mockCollection.status == SUCCESS)
            assert(mockCollection.data != null)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun resendOtpFail() {
        otpVerificationViewModel.otpNumber.value = "123456"
        runTest {
            val mockCollection = ApiResponseModel<String>(FAIL, "", null, "")
            whenever(otpVerificationRepository.generateOtp()).thenReturn(mockCollection)
            otpVerificationViewModel.resendOtp { _, _ -> }
            assert(mockCollection.status == FAIL)
            assert(mockCollection.data == null)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun validateOtpFail() {
        otpVerificationViewModel.otpNumber.value = "123456"
        runTest {
            val mockCollection = ApiResponseModel<OtpVerificationModel>(FAIL, "", null, "")
            whenever(otpVerificationRepository.validateOtp("123456")).thenReturn(mockCollection)
            otpVerificationViewModel.validateOtp { _, _ -> }
            assert(mockCollection.status == FAIL)
            assert(mockCollection.data == null)
        }
    }

    @After
    fun tearDown() {
    }
}