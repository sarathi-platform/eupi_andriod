package com.patsurvey.nudge.base

import android.net.Uri
import com.google.gson.JsonSyntaxException
import com.nudge.core.enums.EventFormatterName
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventswriter.EventWriterFactory
import com.nudge.core.eventswriter.IEventFormatter
import com.nudge.core.eventswriter.entities.EventV1
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.analytics.AnalyticsHelper
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.di.DatabaseModule
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.dataModel.RankingEditEvent
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.request.UpdateWorkflowRequest
import com.patsurvey.nudge.network.NetworkResult
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BPC_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.COMMON_ERROR_MSG
import com.patsurvey.nudge.utils.HEADING_QUESTION_TYPE
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PAT_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.RESPONSE_CODE_500
import com.patsurvey.nudge.utils.RESPONSE_CODE_BAD_GATEWAY
import com.patsurvey.nudge.utils.RESPONSE_CODE_CONFLICT
import com.patsurvey.nudge.utils.RESPONSE_CODE_DEACTIVATED
import com.patsurvey.nudge.utils.RESPONSE_CODE_NETWORK_ERROR
import com.patsurvey.nudge.utils.RESPONSE_CODE_NOT_FOUND
import com.patsurvey.nudge.utils.RESPONSE_CODE_NO_DATA
import com.patsurvey.nudge.utils.RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE
import com.patsurvey.nudge.utils.RESPONSE_CODE_TIMEOUT
import com.patsurvey.nudge.utils.RESPONSE_CODE_UNAUTHORIZED
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.TIMEOUT_ERROR_MSG
import com.patsurvey.nudge.utils.UNAUTHORISED_MESSAGE
import com.patsurvey.nudge.utils.UNREACHABLE_ERROR_MSG
import com.patsurvey.nudge.utils.json
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

abstract class BaseRepository{

    // job
    var repoJob: Job? = null

//    private val uiScope = CoroutineScope(Dispatchers.Main + repoJob!!)

    @Inject
    lateinit var apiInterface: ApiService

    @Inject
    lateinit var didiDao:DidiDao

    open fun onServerError(error: ErrorModel?){

     }
    open fun onServerError(errorModel: ErrorModelWithApi?){

     }
    open fun getDidiFromDB(didiId:Int):DidiEntity{
      return didiDao.getDidi(didiId)
    }
    suspend fun <T> safeApiCall(apiCall:suspend ()->Response<T>):NetworkResult<T>{
        try {
            val response=apiCall()
            if(response.isSuccessful){
                val body=response.body()
                body?.let {
                    return NetworkResult.Success(it)
                }
            }
            return error("${response.code()} ${response.message()}")
        }catch (e:Exception){
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error("Api call failed $errorMessage")


    open fun onError(tag: String = "BaseViewModel", message: String) {
        NudgeLogger.e(tag, message)
    }

    open fun onCatchError(e:Exception, api: ApiType) {
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
                        onServerError(
                            ErrorModelWithApi(apiName = api, message = UNREACHABLE_ERROR_MSG,
                            statusCode = e.response()?.code() ?: -1)
                        )

                    RESPONSE_CODE_DEACTIVATED,
                    RESPONSE_CODE_500,
                    RESPONSE_CODE_BAD_GATEWAY,
                    RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE ->
                        onServerError(ErrorModelWithApi(apiName = api, statusCode = e.response()?.code() ?: -1, message = e.response()?.message()))

                    else ->
                        onServerError(
                            ErrorModelWithApi(apiName = api, statusCode = e.response()?.code() ?: -1,
                            message = e.message?: COMMON_ERROR_MSG
                            )
                        )
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
    }

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        when (e) {
            is HttpException -> {
                when (e.response()?.code() ?: 0) {
                    RESPONSE_CODE_UNAUTHORIZED -> {
                        if(!RetryHelper.tokenExpired.value) {
                            RetryHelper.tokenExpired.value = true
                        }
                        onServerError(ErrorModel(e.response()?.code() ?: 0, UNAUTHORISED_MESSAGE))
                    }
                    RESPONSE_CODE_CONFLICT -> {
                        if(!RetryHelper.tokenExpired.value) {
                            RetryHelper.tokenExpired.value = true
                        }
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

    private fun getEventFormatter(): IEventFormatter {
        return EventWriterFactory().createEventWriter(
            NudgeCore.getAppContext(),
            EventFormatterName.JSON_FORMAT_EVENT
        )
    }

    open suspend fun writeEventIntoLogFile(eventV1: EventV1){
     try {


         val eventFormatter: IEventFormatter = getEventFormatter()
         eventFormatter.saveAndFormatEvent(
             event = eventV1,
             listOf(
                 EventWriterName.FILE_EVENT_WRITER,
                 EventWriterName.DB_EVENT_WRITER,
                 EventWriterName.LOG_EVENT_WRITER
             )
         )
     }   catch (exception:Exception)
     {
         NudgeLogger.e("ImageEventWriter",exception.message?:"")
     }
    }

    open suspend fun writeImageEventIntoLogFile(eventV1: EventV1) {
        val  eventFormatter: IEventFormatter = getEventFormatter()
        try {


        eventFormatter.saveAndFormatEvent(
            event = eventV1,
            listOf(
                EventWriterName.FILE_EVENT_WRITER,
                EventWriterName.IMAGE_EVENT_WRITER,
                EventWriterName.DB_EVENT_WRITER,
                EventWriterName.LOG_EVENT_WRITER
            ),
            uri
        )
        uri = null
        }
        catch (exception:Exception){
            NudgeLogger.e("ImageEventWriter",exception.message?:"")
        }

    }

    var uri: Uri? = null

    fun createStepUpdateEvent(stepStatus: String, stepListEntity: StepListEntity, mobileNumber: String): EventV1 {
        val payload = UpdateWorkflowRequest.getUpdateWorkflowRequest(stepListEntity, stepStatus).json()
        return EventV1(
            eventTopic = EventName.WORKFLOW_STATUS_UPDATE.topicName,
            payload = payload,
            mobileNumber = mobileNumber
        )
    }

    fun createRankingFlagEditEvent(villageId: Int, stepType: String, mobileNumber: String): EventV1 {
        val payload = RankingEditEvent(villageId = villageId, type = stepType, status = false).json()
        return EventV1(
            eventTopic = EventName.RANKING_FLAG_EDIT.topicName,
            payload = payload,
            mobileNumber = mobileNumber
        )
    }
}

