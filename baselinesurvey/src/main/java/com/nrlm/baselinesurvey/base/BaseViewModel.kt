package com.nrlm.baselinesurvey.base

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.nrlm.baselinesurvey.BAD_GATEWAY_ERROR_MESSAGE
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.COMMON_ERROR_MSG
import com.nrlm.baselinesurvey.INTERNAL_SERVER_ERROR_MESSAGE
import com.nrlm.baselinesurvey.JSON_PARSING_EXCEPTION
import com.nrlm.baselinesurvey.RESPONSE_CODE_500
import com.nrlm.baselinesurvey.RESPONSE_CODE_BAD_GATEWAY
import com.nrlm.baselinesurvey.RESPONSE_CODE_CONFLICT
import com.nrlm.baselinesurvey.RESPONSE_CODE_DEACTIVATED
import com.nrlm.baselinesurvey.RESPONSE_CODE_NETWORK_ERROR
import com.nrlm.baselinesurvey.RESPONSE_CODE_NOT_FOUND
import com.nrlm.baselinesurvey.RESPONSE_CODE_NO_DATA
import com.nrlm.baselinesurvey.RESPONSE_CODE_SERVER_ERROR
import com.nrlm.baselinesurvey.RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE
import com.nrlm.baselinesurvey.RESPONSE_CODE_TIMEOUT
import com.nrlm.baselinesurvey.RESPONSE_CODE_UNAUTHORIZED
import com.nrlm.baselinesurvey.SUCCESS_CODE
import com.nrlm.baselinesurvey.TEMP_UNAVAILABLE_ERROR_MESSAGE
import com.nrlm.baselinesurvey.TIMEOUT_ERROR_MSG
import com.nrlm.baselinesurvey.UNAUTHORISED_MESSAGE
import com.nrlm.baselinesurvey.UNREACHABLE_ERROR_MSG
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.model.datamodel.ErrorModel
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.ui.common_components.common_events.ApiStatusEvent
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchDataUseCase
import com.nrlm.baselinesurvey.utils.BaselineLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseViewModel() : ViewModel() {
    abstract fun <T> onEvent(event: T)

    open fun updateQuestionStateForCondition(conditionResult: Boolean, conditionsDto: ConditionsDto?) {}

    open fun performSearchQuery(queryTerm: String, isFilterApplied: Boolean, fromScreen: String) {}

    open fun filterList(fromScreen: String) {}

    val baseOtpNumber = mutableStateOf("")
    val baseSummarySecond = mutableStateOf(0)

    var job: Job? = null
    var currentApiCount = 0
    var networkErrorMessage = mutableStateOf(BLANK_STRING)
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        BaselineLogger.e("BaseViewModel", "exceptionHandler: ${e.message}", e)
        onCatchError(e)

    }


    open fun onError(tag: String = "BaseViewModel", message: String) {
        BaselineLogger.e(tag, message)
    }
    open fun onServerError(error: ErrorModel?) {
        viewModelScope.launch(Dispatchers.Main) {
            onEvent(LoaderEvent.UpdateLoaderState(false))
            onEvent(ApiStatusEvent.showApiStatus(RESPONSE_CODE_SERVER_ERROR, error?.message ?: ""))
        }
    }




//    abstract fun onServerError(errorModel: ErrorModelWithApi?)

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    private fun parseException(e: Throwable): ErrorModel {
        when (e) {
            is HttpException -> {
                BaselineLogger.e("BaseViewModel", "onCatchError code: ${e.response()?.code() ?: 0}")
                when (e.response()?.code() ?: 0) {
                    RESPONSE_CODE_UNAUTHORIZED -> {
                        return ErrorModel(e.response()?.code() ?: 0, UNAUTHORISED_MESSAGE)
                    }
                    RESPONSE_CODE_CONFLICT -> {
                        return ErrorModel(e.response()?.code() ?: 0, UNAUTHORISED_MESSAGE)
                    }
                    RESPONSE_CODE_NOT_FOUND ->
                        return ErrorModel(
                            message = UNREACHABLE_ERROR_MSG,
                            statusCode = e.response()?.code() ?: -1
                        )

                    RESPONSE_CODE_DEACTIVATED ->
                        return ErrorModel(
                            message = UNREACHABLE_ERROR_MSG,
                            statusCode = e.response()?.code() ?: -1
                        )

                    RESPONSE_CODE_500 ->
                        return ErrorModel(
                            message = INTERNAL_SERVER_ERROR_MESSAGE,
                            statusCode = e.response()?.code() ?: -1
                        )

                    RESPONSE_CODE_BAD_GATEWAY ->
                        return ErrorModel(
                            message = BAD_GATEWAY_ERROR_MESSAGE,
                            statusCode = e.response()?.code() ?: -1
                        )
                    RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE ->
                        return ErrorModel(
                            statusCode = e.response()?.code() ?: -1,
                            message = TEMP_UNAVAILABLE_ERROR_MESSAGE
                        )

                    else ->
                        return ErrorModel(
                            statusCode = e.response()?.code() ?: -1,
                            message = e.message ?: COMMON_ERROR_MSG
                        )
                }
            }
            is SocketTimeoutException -> {
                return ErrorModel(statusCode = RESPONSE_CODE_TIMEOUT, message = TIMEOUT_ERROR_MSG)
            }
            is IOException -> {
                return ErrorModel(statusCode = RESPONSE_CODE_NETWORK_ERROR)
            }
            is JsonSyntaxException ->{
                return ErrorModel(
                    -1,
                    statusCode = RESPONSE_CODE_NO_DATA,
                    message = JSON_PARSING_EXCEPTION
                )
            }

            else -> return ErrorModel(-1, e.message)
        }
    }

    open fun onCatchError(e: Throwable) {
        BaselineLogger.e("BaseViewModel", "onCatchError: ${e.message}")
        onServerError(parseException(e))
    }

    fun refreshData(fetchDataUseCase: FetchDataUseCase) {
        currentApiCount = 0
        onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                callSurveyApi(fetchDataUseCase)
                fetchMissionData(fetchDataUseCase)
                fetchCastes(fetchDataUseCase)
                fetchUserDetail(fetchDataUseCase)
                fetchSurveyeeList(fetchDataUseCase)
                fetchContentData(fetchDataUseCase)


        }

    }

    private suspend fun updateLoaderEvent() {
        if (currentApiCount == 6) {
            withContext(Dispatchers.Main) {
                // onEvent(LoaderEvent.UpdateLoaderState(false))
                onEvent(ApiStatusEvent.showApiStatus(SUCCESS_CODE.toInt(), ""))
            }
        }
    }

    private fun callSurveyApi(fetchDataUseCase: FetchDataUseCase) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val baselineSurveyRequestBodyModel = SurveyRequestBodyModel(
                languageId = fetchDataUseCase.fetchSurveyFromNetworkUseCase.getAppLanguageId(),
                surveyName = "BASELINE",
                referenceId = fetchDataUseCase.fetchSurveyFromNetworkUseCase.getStateId(),
                referenceType = "STATE"
            )
            fetchDataUseCase.fetchSurveyFromNetworkUseCase.invoke(
                baselineSurveyRequestBodyModel
            )

            val hamletSurveyRequestBodyModel = SurveyRequestBodyModel(
                languageId = fetchDataUseCase.fetchSurveyFromNetworkUseCase.getAppLanguageId(),
                surveyName = "HAMLET",
                referenceId = fetchDataUseCase.fetchSurveyFromNetworkUseCase.getStateId(),
                referenceType = "STATE"
            )
            fetchDataUseCase.fetchSurveyFromNetworkUseCase.invoke(
                hamletSurveyRequestBodyModel
            )
            currentApiCount++
            updateLoaderEvent()
        }
    }

    private fun fetchUserDetail(fetchDataUseCase: FetchDataUseCase) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchUserDetailFromNetworkUseCase.invoke()
            currentApiCount++
            updateLoaderEvent()
        }
    }

    private fun fetchCastes(fetchDataUseCase: FetchDataUseCase) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchCastesFromNetworkUseCase.invoke(false)
            currentApiCount++
            updateLoaderEvent()
        }
    }

    private fun fetchMissionData(fetchDataUseCase: FetchDataUseCase) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchMissionDataFromNetworkUseCase.invoke()
            currentApiCount++
            updateLoaderEvent()
        }
    }

    private fun fetchSurveyeeList(fetchDataUseCase: FetchDataUseCase) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchSurveyeeListFromNetworkUseCase.invoke()
            currentApiCount++
            updateLoaderEvent()

        }
    }

    private fun fetchContentData(fetchDataUseCase: FetchDataUseCase) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchContentnDataFromNetworkUseCase.invoke()
            currentApiCount++
            updateLoaderEvent()
        }
    }
}

