package com.patsurvey.nudge.base

import com.google.gson.JsonSyntaxException
import com.nudge.core.EventSyncStatus
import com.nudge.core.SELECTION_MISSION
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.getDependentEventsId
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.toDate
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.analytics.AnalyticsHelper
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.model.request.UpdateWorkflowRequest
import com.patsurvey.nudge.network.NetworkResult
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.COMMON_ERROR_MSG
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
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
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TIMEOUT_ERROR_MSG
import com.patsurvey.nudge.utils.UNAUTHORISED_MESSAGE
import com.patsurvey.nudge.utils.UNREACHABLE_ERROR_MSG
import com.patsurvey.nudge.utils.getParentEntityMapForEvent
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

    open suspend fun <T> createEvent(eventItem: T, eventName: EventName, eventType: EventType): Events? {
        return Events.getEmptyEvent()
    }

    open suspend fun <T> createEventDependency(eventItem: T, eventName: EventName, dependentEvent: Events): List<EventDependencyEntity> {
        return emptyList()
    }

    open suspend fun <T> insertEventIntoDb(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ) {

    }

    suspend fun <T> getPatSaveAnswersEvent(eventItem: T, eventName: EventName, eventType: EventType, patSummarySaveRequest: PATSummarySaveRequest, prefRepo: PrefRepo): Events {
        val requestPayload = patSummarySaveRequest.json()

        var savePatSummeryEvent = Events(
            name = eventName.name,
            type = eventType.name,
            createdBy = prefRepo.getUserId(),
            mobile_number = prefRepo.getMobileNumber(),
            request_payload = requestPayload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = listOf(),
                request_payload_size = requestPayload.getSizeInLong(),
                parentEntity = getParentEntityMapForEvent((eventItem as DidiEntity), eventName)
            ).json()
        )

        return savePatSummeryEvent
    }

    suspend fun <T> getPatSaveScoreEvent(eventItem: T, eventName: EventName, eventType: EventType, patScoreSaveEvent: EditDidiWealthRankingRequest, prefRepo: PrefRepo): Events {
        val requestPayload = patScoreSaveEvent.json()
        var savePatScoreEvent = Events(
            name = eventName.name,
            type = eventType.name,
            createdBy = prefRepo.getUserId(),
            mobile_number = prefRepo.getMobileNumber(),
            request_payload = requestPayload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = listOf(),
                request_payload_size = requestPayload.getSizeInLong(),
                parentEntity = getParentEntityMapForEvent(eventItem, eventName)
            ).json()
        )

        return savePatScoreEvent
    }

    open suspend fun insertEventIntoDb(event: Events?, eventDependencies: List<EventDependencyEntity>) {
        val eventObserver = NudgeCore.getEventObserver()

        if (event == null)
            return
        if (event.id == BLANK_STRING)
            return

        eventObserver?.addEvent(event)
        if (eventDependencies.isNotEmpty()) {
            eventObserver?.addEventDependencies(eventDependencies)
        }

    }

    fun <T> createWorkflowEvent(eventItem: T, stepStatus: StepStatus, eventName: EventName, eventType: EventType, prefRepo: PrefRepo): Events? {

        if (eventType != EventType.STATEFUL)
            return null

        if (eventItem !is StepListEntity)
            return null

        if (eventName != EventName.WORKFLOW_STATUS_UPDATE)
            return null

        val requestPayload = UpdateWorkflowRequest.getUpdateWorkflowRequest(
            stepListEntity = (eventItem as StepListEntity),
            status = stepStatus.name
        ).json()

        val updateWorkflowEvent = Events(
            name = eventName.name,
            type = eventType.name,
            createdBy = prefRepo.getUserId(),
            mobile_number = prefRepo.getMobileNumber(),
            request_payload = requestPayload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = listOf(),
                request_payload_size = requestPayload.getSizeInLong(),
                parentEntity = getParentEntityMapForEvent(eventItem, eventName)
            ).json()
        )

        return updateWorkflowEvent

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

}

