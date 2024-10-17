package com.patsurvey.nudge.base

import android.net.Uri
import com.google.gson.JsonSyntaxException
import com.nudge.core.EventSyncStatus
import com.nudge.core.SELECTION_MISSION
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventFormatterName
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.enums.EventWriterName
import com.nudge.core.enums.getDependsOnEventNameForEvent
import com.nudge.core.eventswriter.EventWriterFactory
import com.nudge.core.eventswriter.IEventFormatter
import com.nudge.core.eventswriter.entities.EventV1
import com.nudge.core.getEventDependencyEntityListFromEvents
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.toDate
import com.nudge.core.value
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.analytics.AnalyticsHelper
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.dataModel.RankingEditEvent
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.model.request.UpdateWorkflowRequest
import com.patsurvey.nudge.model.request.getAddDidiRequestPayloadFromString
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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
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

    @Inject
    lateinit var eventsDao: EventsDao

    @Inject
    lateinit var eventDependencyDao: EventDependencyDao



    open fun onServerError(error: ErrorModel?){

     }
    open fun onServerError(errorModel: ErrorModelWithApi?){

     }
    open fun getDidiFromDB(didiId:Int):DidiEntity{
      return didiDao.getDidi(didiId)
    }

    open suspend fun <T> createEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ): Events? {
        return Events.getEmptyEvent()
    }

    open suspend fun <T> createEventDependency(
        eventItem: T,
        eventName: EventName,
        dependentEvent: Events
    ): List<EventDependencyEntity> {
        val eventDependencyList = mutableListOf<EventDependencyEntity>()
        var filteredList = listOf<Events>()
        var dependentEventsName = eventName.getDependsOnEventNameForEvent()
        for (dependsOnEvent in dependentEventsName) {
            val eventList = eventsDao.getAllEventsForEventName(dependsOnEvent.name)
            when (eventName) {
                EventName.CRP_IMAGE -> {
                    filteredList = eventList.filter {
                        val eventPayload =
                            dependentEvent.request_payload?.getAddDidiRequestPayloadFromString()
                        it.payloadLocalId == eventPayload?.cohortDeviceId
                    }
                }

                EventName.BPC_IMAGE -> {
                    filteredList = eventList.filter {
                        it.payloadLocalId == dependentEvent.payloadLocalId
                    }

                }

                else -> {
                    filteredList = emptyList()
                }
            }

            if (filteredList.isNotEmpty()) {
                break
            }

        }


        if (filteredList.isNotEmpty()) {

            val immediateDependentOn = ArrayList<Events>()
            immediateDependentOn.add(filteredList.first())

            eventDependencyList.addAll(
                immediateDependentOn.getEventDependencyEntityListFromEvents(
                    dependentEvent
                )
            )
        }
        return eventDependencyList
    }

    open suspend fun <T> saveEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ) {

    }

    suspend fun <T> getPatSaveAnswersEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType,
        patSummarySaveRequest: PATSummarySaveRequest,
        prefRepo: PrefRepo
    ): Events {
        val requestPayload = patSummarySaveRequest.json()

        var savePatSummeryEvent = Events(
            name = eventName.name,
            type = eventName.topicName,
            createdBy = prefRepo.getUserId(),
            mobile_number = prefRepo.getMobileNumber(),
            request_payload = requestPayload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            payloadLocalId = patSummarySaveRequest.deviceId,
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

    suspend fun <T> getPatSaveScoreEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType,
        patScoreSaveEvent: EditDidiWealthRankingRequest,
        prefRepo: PrefRepo
    ): Events {
        val requestPayload = patScoreSaveEvent.json()
        var savePatScoreEvent = Events(
            name = eventName.name,
            type = eventName.topicName,
            createdBy = prefRepo.getUserId(),
            mobile_number = prefRepo.getMobileNumber(),
            request_payload = requestPayload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            payloadLocalId = patScoreSaveEvent.deviceId,
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = listOf(),
                request_payload_size = requestPayload.getSizeInLong(),
                parentEntity = getParentEntityMapForEvent(eventItem, eventName)
            ).json()
        )

        return savePatScoreEvent
    }

    open suspend fun insertEventIntoDb(
        event: Events?,
        eventDependencies: List<EventDependencyEntity>
    ) {
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

    fun <T> createWorkflowEvent(
        eventItem: T,
        stepStatus: StepStatus,
        eventName: EventName,
        eventType: EventType,
        prefRepo: PrefRepo
    ): Events? {

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
            type = eventName.topicName,
            createdBy = prefRepo.getUserId(),
            mobile_number = prefRepo.getMobileNumber(),
            request_payload = requestPayload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            payloadLocalId = "",
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

    private fun getEventFormatter(): IEventFormatter {
        return EventWriterFactory().createEventWriter(
            NudgeCore.getAppContext(),
            EventFormatterName.JSON_FORMAT_EVENT,
            eventsDao = eventsDao,
            eventDependencyDao
        )
    }

    open suspend fun saveEventToMultipleSources(
        event: Events,
        eventDependencies: List<EventDependencyEntity>
    ) {
        try {


            val eventFormatter: IEventFormatter = getEventFormatter()
            eventFormatter.saveAndFormatEvent(
                event = event,
                dependencyEntity = eventDependencies,
                listOf(
                    EventWriterName.FILE_EVENT_WRITER,
                    EventWriterName.DB_EVENT_WRITER,
                    EventWriterName.LOG_EVENT_WRITER
                ),

                )
        } catch (exception: Exception) {
            NudgeLogger.e("ImageEventWriter", exception.message ?: "")
        }
    }

    open suspend fun writeImageEventIntoLogFile(
        event: Events,
        eventDependencies: List<EventDependencyEntity>
    ) {
        val eventFormatter: IEventFormatter = getEventFormatter()
        try {


            eventFormatter.saveAndFormatEvent(
                event = event,
                dependencyEntity = eventDependencies,
                listOf(
                    EventWriterName.FILE_EVENT_WRITER,
                    EventWriterName.IMAGE_EVENT_WRITER,
                    EventWriterName.DB_EVENT_WRITER,
                    EventWriterName.LOG_EVENT_WRITER
                ),

                uri
            )
            uri = null
        } catch (exception: Exception) {
            NudgeLogger.e("ImageEventWriter", exception.message ?: "")
        }

    }

    var uri: Uri? = null
    fun createStepUpdateEvent(
        stepStatus: String,
        stepListEntity: StepListEntity,
        mobileNumber: String
    ): EventV1 {
        val payload =
            UpdateWorkflowRequest.getUpdateWorkflowRequest(stepListEntity, stepStatus).json()
        return EventV1(
            eventTopic = EventName.WORKFLOW_STATUS_UPDATE.topicName,
            payload = payload,
            mobileNumber = mobileNumber
        )
    }


    suspend fun getTolaDeviceIdMap(villageId: Int, tolaDao: TolaDao): Map<Int, String> {
        val tolaList = tolaDao.getAllTolasForVillage(villageId)
        val tolaDeviceIdMap = mutableMapOf<Int, String>()
        tolaList.forEach {

            tolaDeviceIdMap.put(it.id, it.localUniqueId.value())

        }
        return tolaDeviceIdMap
    }

    fun createRankingFlagEditEvent(
        eventItem: StepListEntity,
        didiList: List<DidiEntity>,
        tolaDeviceIdMap: Map<Int, String>,
        villageId: Int,
        stepType: String,
        mobileNumber: String,
        userID: String
    ): List<Events> {

        val rankingEditEventList = ArrayList<Events>()

        didiList.forEach { didi ->
            val payload =
                RankingEditEvent.getRankingEditEvent(
                    villageId = villageId,
                    stepType = stepType,
                    didiEntity = didi,
                    tolaDeviceId = tolaDeviceIdMap[didi.cohortId].value()
                ).json()
            rankingEditEventList.add(
                Events(
                    name = EventName.RANKING_FLAG_EDIT_NEW.name,/*EventName.RANKING_FLAG_EDIT.name,*/
                    type = EventName.RANKING_FLAG_EDIT_NEW.topicName,/*EventName.RANKING_FLAG_EDIT.topicName,*/
                    createdBy = userID,
                    mobile_number = mobileNumber,
                    request_payload = payload,
                    status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    result = null,
                    consumer_status = BLANK_STRING,
                    payloadLocalId = "",
                    metadata = MetadataDto(
                        mission = SELECTION_MISSION,
                        depends_on = listOf(),
                        request_payload_size = payload.getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(
                            eventItem,
                            EventName.RANKING_FLAG_EDIT_NEW
                        )
                    ).json()
                )
            )
        }

        return rankingEditEventList

    }

    fun createImageUploadEvent(
        payload: String,
        mobileNumber: String,
        userID: String,
        payloadlocalId: String,
        eventName: EventName
    ): Events {
        return Events(
            name = eventName.name,
            type = eventName.topicName,
            createdBy = userID,
            mobile_number = mobileNumber,
            request_payload = payload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            payloadLocalId = payloadlocalId,
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = listOf(),
                request_payload_size = payload.getSizeInLong(),
                parentEntity = mapOf()
            ).json()
        )


    }

    fun getSurveyId(questionId: Int, questionListDao: QuestionListDao): Int {
        return questionListDao.getQuestion(questionId).surveyId ?: 0
    }
}

