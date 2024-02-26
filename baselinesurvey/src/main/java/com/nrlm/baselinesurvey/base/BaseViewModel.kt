package com.nrlm.baselinesurvey.base

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.gson.JsonSyntaxException
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.COMMON_ERROR_MSG
import com.nrlm.baselinesurvey.RESPONSE_CODE_500
import com.nrlm.baselinesurvey.RESPONSE_CODE_BAD_GATEWAY
import com.nrlm.baselinesurvey.RESPONSE_CODE_CONFLICT
import com.nrlm.baselinesurvey.RESPONSE_CODE_DEACTIVATED
import com.nrlm.baselinesurvey.RESPONSE_CODE_NETWORK_ERROR
import com.nrlm.baselinesurvey.RESPONSE_CODE_NOT_FOUND
import com.nrlm.baselinesurvey.RESPONSE_CODE_NO_DATA
import com.nrlm.baselinesurvey.RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE
import com.nrlm.baselinesurvey.RESPONSE_CODE_TIMEOUT
import com.nrlm.baselinesurvey.RESPONSE_CODE_UNAUTHORIZED
import com.nrlm.baselinesurvey.TIMEOUT_ERROR_MSG
import com.nrlm.baselinesurvey.UNAUTHORISED_MESSAGE
import com.nrlm.baselinesurvey.UNREACHABLE_ERROR_MSG
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.model.datamodel.ErrorModel
import com.nrlm.baselinesurvey.utils.BaselineLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseViewModel : ViewModel(){

    abstract fun <T> onEvent(event: T)

    open fun updateQuestionStateForCondition(conditionResult: Boolean, conditionsDto: ConditionsDto?) {}

    open fun performSearchQuery(queryTerm: String, isFilterApplied: Boolean, fromScreen: String) {}

    open fun filterList(fromScreen: String) {}

    val baseOtpNumber = mutableStateOf("")
    val baseSummarySecond = mutableStateOf(0)

    var job: Job? = null
    var networkErrorMessage = mutableStateOf(BLANK_STRING)
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        when (e) {
            is HttpException -> {
                when (e.response()?.code() ?: 0) {
                    RESPONSE_CODE_UNAUTHORIZED -> {
                        onServerError(ErrorModel(e.response()?.code() ?: 0, UNAUTHORISED_MESSAGE))
                    }
                    RESPONSE_CODE_CONFLICT -> {
                        onServerError(ErrorModel(e.response()?.code() ?: 0, UNAUTHORISED_MESSAGE))
                    }
                    RESPONSE_CODE_NOT_FOUND ->
                        onServerError(ErrorModel(message = UNREACHABLE_ERROR_MSG,
                            statusCode = e.response()?.code() ?: -1))

                    RESPONSE_CODE_DEACTIVATED,
                    RESPONSE_CODE_500,
                    RESPONSE_CODE_BAD_GATEWAY,
                    RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE ->
                        onServerError(ErrorModel(statusCode = e.response()?.code() ?: -1, message = e.response()?.message()))

                    else ->
                        onServerError(ErrorModel(statusCode = e.response()?.code() ?: -1,
                            message = e.message?: COMMON_ERROR_MSG))
                }
            }
            is SocketTimeoutException -> {
                onServerError(ErrorModel(statusCode = RESPONSE_CODE_TIMEOUT,message = TIMEOUT_ERROR_MSG))
            }
            is IOException -> {
                onServerError(ErrorModel(statusCode = RESPONSE_CODE_NETWORK_ERROR))
            }
            is JsonSyntaxException ->{
                onServerError(ErrorModel(-1, e.message, statusCode = RESPONSE_CODE_NO_DATA))
            }
            else -> onServerError(ErrorModel(-1, e.message))
        }
    }

    open fun onError(tag: String = "BaseViewModel", message: String) {
        BaselineLogger.e(tag, message)
    }
    open fun onServerError(error: ErrorModel?) {
    }

//    abstract fun onServerError(errorModel: ErrorModelWithApi?)

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    open fun onCatchError(e:Exception) {
        BaselineLogger.e("BaseViewModel", "onCatchError: ${e.message}")
        when (e) {
            is HttpException -> {
                BaselineLogger.e("BaseViewModel", "onCatchError code: ${e.response()?.code() ?: 0}")
                when (e.response()?.code() ?: 0) {
                    RESPONSE_CODE_UNAUTHORIZED -> {
                        onServerError(ErrorModel(e.response()?.code() ?: 0, UNAUTHORISED_MESSAGE))
                    }
                    RESPONSE_CODE_CONFLICT -> {
                        onServerError(ErrorModel(e.response()?.code() ?: 0, UNAUTHORISED_MESSAGE))
                    }
                    RESPONSE_CODE_NOT_FOUND ->
                        onServerError(ErrorModel(message = UNREACHABLE_ERROR_MSG,
                            statusCode = e.response()?.code() ?: -1))

                    RESPONSE_CODE_DEACTIVATED,
                    RESPONSE_CODE_500,
                    RESPONSE_CODE_BAD_GATEWAY,
                    RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE ->
                        onServerError(ErrorModel(statusCode = e.response()?.code() ?: -1, message = e.response()?.message()))

                    else ->
                        onServerError(ErrorModel(statusCode = e.response()?.code() ?: -1,
                            message = e.message?: COMMON_ERROR_MSG))
                }
            }
            is SocketTimeoutException -> {
                onServerError(ErrorModel(statusCode = RESPONSE_CODE_TIMEOUT,message = TIMEOUT_ERROR_MSG))
            }
            is IOException -> {
                onServerError(ErrorModel(statusCode = RESPONSE_CODE_NETWORK_ERROR))
            }
            is JsonSyntaxException ->{
                onServerError(ErrorModel(-1, e.message, statusCode = RESPONSE_CODE_NO_DATA))
            }
            else -> onServerError(ErrorModel(-1, e.message))
        }
    }

    /*open fun onCatchError(e:Exception, api: ApiType) {
        NudgeLogger.d("BaseViewModel", "onCatchError: message: ${e.message}, api: ${api.name}")
        AnalyticsHelper.logServiceFailedEvent(exception = e, apiType = api)
        when (e) {
            is HttpException -> {
                NudgeLogger.d("BaseViewModel", "onCatchError code: ${e.response()?.code() ?: 0}, api: ${api.name}")
                when (e.response()?.code() ?: 0) {
                    RESPONSE_CODE_UNAUTHORIZED -> {
                        if(!RetryHelper.tokenExpired.value && api != ApiType.LOGOUT_API) {
                            RetryHelper.tokenExpired.value = true
                        }
                        onServerError(ErrorModel(e.response()?.code() ?: 0, UNAUTHORISED_MESSAGE))
                    }
                    RESPONSE_CODE_CONFLICT -> {
                        if(!RetryHelper.tokenExpired.value && api != ApiType.LOGOUT_API) {
                            RetryHelper.tokenExpired.value = true
                        }
                        onServerError(ErrorModel(e.response()?.code() ?: 0, message = e.response()?.message()))
                    }
                    RESPONSE_CODE_NOT_FOUND ->
                        onServerError(ErrorModelWithApi(apiName = api, message = UNREACHABLE_ERROR_MSG,
                            statusCode = e.response()?.code() ?: -1))

                    RESPONSE_CODE_DEACTIVATED,
                    RESPONSE_CODE_500,
                    RESPONSE_CODE_BAD_GATEWAY,
                    RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE ->
                        onServerError(ErrorModelWithApi(apiName = api, statusCode = e.response()?.code() ?: -1, message = e.response()?.message()))

                    else ->
                        onServerError(ErrorModelWithApi(apiName = api, statusCode = e.response()?.code() ?: -1,
                            message = e.message?: COMMON_ERROR_MSG))
                }
            }
            is SocketTimeoutException -> {
                onServerError(ErrorModelWithApi(apiName = api, statusCode = RESPONSE_CODE_TIMEOUT,message = TIMEOUT_ERROR_MSG))
            }
            is IOException -> {
                onServerError(ErrorModelWithApi(apiName = api, statusCode = RESPONSE_CODE_NETWORK_ERROR))
            }
            is JsonSyntaxException ->{
                onServerError(ErrorModelWithApi(-1, apiName = api, e.message, statusCode = RESPONSE_CODE_NO_DATA))
            }
            is ApiResponseFailException -> {
                onServerError(ErrorModelWithApi(code = -1, apiName = api, e.message))
            }
            else -> onServerError(ErrorModelWithApi(-1, apiName = api, e.message))
        }
    }*/


}

