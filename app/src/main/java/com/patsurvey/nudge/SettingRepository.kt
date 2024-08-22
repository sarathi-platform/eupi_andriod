package com.patsurvey.nudge

import android.text.TextUtils
import com.google.gson.Gson
import com.nudge.core.EventSyncStatus
import com.nudge.core.KEY_PARENT_ENTITY_ADDRESS
import com.nudge.core.KEY_PARENT_ENTITY_DADA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_DIDI_NAME
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_NAME
import com.nudge.core.SELECTION_MISSION
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.getDependentEventsId
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.enums.getDependsOnEventNameForEvent
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.getEventDependencyEntityListFromEvents
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.LastSelectedTolaDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.AddDidiRequest
import com.patsurvey.nudge.model.request.DidiImageUploadRequest
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.SaveMatchSummaryRequest
import com.patsurvey.nudge.model.request.getAddDidiRequestPayloadFromString
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.findImageLocationFromPath
import com.patsurvey.nudge.utils.getPatScoreEventName
import com.patsurvey.nudge.utils.getPatScoreSaveEvent
import com.patsurvey.nudge.utils.getPatSummarySaveEventPayload
import com.patsurvey.nudge.utils.uriFromFile
import java.io.File
import javax.inject.Inject


class SettingRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val lastSelectedTolaDao: LastSelectedTolaDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao,
    val questionDao: QuestionListDao,
    val numericAnswerDao: NumericAnswerDao,
) : BaseRepository() {

    suspend fun regenerateAllEvent(coreSharedPrefs: CoreSharedPrefs) {
        try {


            villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2).forEach {
                coreSharedPrefs.setBackupFileName(
                    getDefaultBackUpFileName(
                        "regenerate_${it.id}_" + prefRepo.getMobileNumber(),
                        coreSharedPrefs.getUserType() ?: BLANK_STRING
                    )
                )
                coreSharedPrefs.setImageBackupFileName(
                    getDefaultImageBackUpFileName(
                        "regenerate_${it.id}_" + prefRepo.getMobileNumber(),
                        coreSharedPrefs.getUserType() ?: BLANK_STRING
                    )
                )

                if (prefRepo.isUserBPC()) {
                    generatePatEvents(it.id)
                    generateWorkFlowStatusEvent(it.id)
                    generateDidiImageEvent(it.id)
                    generateBPCMismatchScoreEvent(it.id)
                } else {
                    generateAddTolaEvent(it.id)
                    generateAddDidiEvent(it.id)
                    generateWealthRankingEvent(it.id)
                    generatePatEvents(it.id)
                    generateVOEvents(it.id)
                    generateRankingEditEvent(it.id)
                    generateWorkFlowStatusEvent(it.id)
                    generateDidiImageEvent(it.id)
                }
            }

        } catch (exception: Exception) {
            NudgeLogger.e("RegenerateEvent", exception.message ?: "")
        } finally {

            coreSharedPrefs.setBackupFileName(
                getDefaultBackUpFileName(
                    prefRepo.getMobileNumber(),
                    coreSharedPrefs.getUserType()
                )
            )
            coreSharedPrefs.setImageBackupFileName(
                getDefaultImageBackUpFileName(
                    prefRepo.getMobileNumber(),
                    coreSharedPrefs.getUserType()
                )
            )

        }

    }

    private suspend fun generateDidiImageEvent(villageId: Int) {
        didiDao.fetchAllDidiNeedsToPostImageVillage(true, villageId).forEach { didiEntity ->
            if (!TextUtils.isEmpty(didiEntity.localPath)) {
                val selectedTolaEntity =
                    tolaDao.fetchSingleTolaFromServerId(didiEntity.cohortId)

                val path = findImageLocationFromPath(didiEntity.localPath)
                uri = uriFromFile(NudgeCore.getAppContext(), File(path[0]))
                val payload = DidiImageUploadRequest.getRequestObjectForDidiUploadImage(
                    didi = didiEntity,
                    location = path[1],
                    filePath = path[0],
                    userType = if (prefRepo.isUserBPC()) USER_BPC else USER_CRP,
                    tolaServerId = selectedTolaEntity?.serverId,
                    cohortdeviceId = selectedTolaEntity?.localUniqueId
                ).json()
                val event = createImageUploadEvent(
                    payload = payload,
                    payloadlocalId = didiEntity.localUniqueId,
                    mobileNumber = prefRepo.getMobileNumber(),
                    userID = prefRepo.getUserId(),
                    eventName = if (prefRepo.isUserBPC()) EventName.BPC_IMAGE else EventName.CRP_IMAGE,
                )
                event.let {
                    val eventDependencies = this.createEventDependency(
                        didiEntity,
                        if (prefRepo.isUserBPC()) EventName.BPC_IMAGE else EventName.CRP_IMAGE,
                        it
                    )
                    writeImageEventIntoLogFile(it, eventDependencies)

                }
            }
        }
    }

    private suspend fun generateAddTolaEvent(villageId: Int) {
        tolaDao.getAllTolasForVillage(villageId).forEach {

            saveEvent(
                eventItem = it,
                eventName = EventName.ADD_TOLA,
                eventType = EventType.STATEFUL
            )
        }

    }

    private suspend fun generateAddDidiEvent(villageId: Int) {
        didiDao.getAllDidisForVillage(villageId).forEach {
            saveEvent(
                eventItem = it,
                eventName = EventName.ADD_DIDI,
                eventType = EventType.STATEFUL
            )
        }

    }

    private suspend fun generateWealthRankingEvent(villageId: Int) {
        didiDao.getRankedDidi(villageId = villageId).forEach {
            saveEvent(
                eventItem = it,
                eventName = EventName.SAVE_WEALTH_RANKING,
                eventType = EventType.STATEFUL
            )
        }
    }

    private suspend fun generatePatEvents(villageId: Int) {
        didiDao.fetchDidisListForPatEvent(villageId = villageId).forEach {
            saveEvent(it, eventName = EventName.SAVE_PAT_ANSWERS, eventType = EventType.STATEFUL)
            saveEvent(
                eventItem = it,
                eventName = getPatScoreEventName(it, prefRepo.isUserBPC()),
                eventType = EventType.STATEFUL
            )

        }
    }

    private suspend fun generateVOEvents(villageId: Int) {
        didiDao.fetchVOEndorseSummaryStatusDidi(villageId = villageId).forEach {
            saveEvent(
                eventItem = it,
                eventName = EventName.SAVE_VO_ENDORSEMENT,
                eventType = EventType.STATEFUL
            )
        }
    }

    private suspend fun generateRankingEditEvent(villageId: Int) {
        stepsListDao.getAllStepsForVillage(villageId).forEach {
            val event = createRankingFlagEditEvent(
                it,
                it.villageId,
                stepType = StepType.getStepTypeFromId(it.id).name,
                prefRepo.getMobileNumber() ?: BLANK_STRING,
                prefRepo.getUserId()
            )

            saveEventToMultipleSources(event, listOf())

        }
    }

    private suspend fun generateWorkFlowStatusEvent(villageId: Int) {
        stepsListDao.getAllStepsForVillage(villageId).forEach {
            val workFlowEvent = createWorkflowEvent(
                eventItem = it,
                eventName = EventName.WORKFLOW_STATUS_UPDATE,
                eventType = EventType.STATEFUL,
                stepStatus = StepStatus.getStepStatusFromOrdinal(it.isComplete),
                prefRepo = prefRepo
            )
            workFlowEvent?.let {
                saveEventToMultipleSources(it, listOf())
            }

        }
    }

    private suspend fun generateBPCMismatchScoreEvent(villageId: Int) {
        val passingScore = questionDao.getPassingScore()
        val bpcStep =
            stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.last()
        val didiList = didiDao.getAllDidisForVillage(villageId)
        val eventItem = SaveMatchSummaryRequest.getSaveMatchSummaryRequestForBpc(
            villageId = villageId,
            stepListEntity = bpcStep,
            didiList = didiList.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal },
            questionPassionScore = passingScore
        )

        saveEvent(eventItem, EventName.SAVE_BPC_MATCH_SCORE, EventType.STATEFUL)


    }


    fun getSelectedVillage(): VillageEntity {
        return this.prefRepo.getSelectedVillage()
    }

    override suspend fun <T> saveEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ) {
        val event = this.createEvent(
            eventItem,
            eventName,
            eventType
        )

        if (event?.id?.equals(BLANK_STRING) != true) {
            event?.let {
                val eventDependencies = this.createEventDependency(eventItem, eventName, it)
                saveEventToMultipleSources(it, eventDependencies)

            }
        }
    }

    override suspend fun <T> createEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ): Events? {

        if (eventType != EventType.STATEFUL)
            return super.createEvent(eventItem, eventName, eventType)

        var requestPayload = ""
        var payloadLocalId = ""
        when (eventName) {
            EventName.ADD_TOLA -> {

                requestPayload =
                    AddCohortRequest.getRequestObjectForTola(eventItem as TolaEntity).json()
                payloadLocalId = (eventItem as TolaEntity).localUniqueId ?: ""
            }

            EventName.ADD_DIDI -> {
                val selectedTolaEntity =
                    tolaDao.fetchSingleTolaFromServerId((eventItem as DidiEntity).cohortId)
                requestPayload = AddDidiRequest.getRequestObjectForDidi(
                    eventItem as DidiEntity,
                    selectedTolaEntity?.serverId,
                    selectedTolaEntity?.localUniqueId
                ).json()
                payloadLocalId = (eventItem as DidiEntity).localUniqueId
            }

            EventName.SAVE_WEALTH_RANKING -> {
                val didiEntity = (eventItem as DidiEntity)
                val selectedTolaEntity = tolaDao.fetchSingleTolaFromServerId(didiEntity.cohortId)
                payloadLocalId = (eventItem as DidiEntity).localUniqueId
                requestPayload = EditDidiWealthRankingRequest
                    .getRequestPayloadForWealthRanking(
                        didiEntity = didiEntity,
                        tolaDeviceId = selectedTolaEntity?.localUniqueId ?: "",
                        tolaServerId = selectedTolaEntity?.serverId ?: 0
                    )
                    .json()
            }

            EventName.SAVE_PAT_ANSWERS -> {
                val requestPayloads = getPatSummarySaveEventPayload(
                    didiEntity = (eventItem as DidiEntity),
                    answerDao = answerDao,
                    numericAnswerDao = numericAnswerDao,
                    questionListDao = questionListDao,
                    prefRepo = prefRepo
                )

                var savePatSummeryEvent = getPatSaveAnswersEvent(
                    eventItem = eventItem,
                    eventName = eventName,
                    eventType = eventType,
                    patSummarySaveRequest = requestPayloads,
                    prefRepo = prefRepo
                )

                val dependsOn = createEventDependency(eventItem, eventName, savePatSummeryEvent)
                val metadata = savePatSummeryEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                savePatSummeryEvent = savePatSummeryEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return savePatSummeryEvent
            }

            EventName.REJECTED_PAT_SCORE, EventName.INPROGRESS_PAT_SCORE, EventName.COMPLETED_PAT_SCORE, EventName.NOT_AVAILBLE_PAT_SCORE -> {
                val selectedTolaEntity =
                    tolaDao.fetchSingleTolaFromServerId((eventItem as DidiEntity).cohortId)

                val requestPayloads = getPatScoreSaveEvent(
                    didiEntity = (eventItem as DidiEntity),
                    questionListDao = questionListDao,
                    prefRepo = prefRepo,
                    selectedTolaEntity?.localUniqueId ?: "",
                    selectedTolaEntity?.serverId ?: 0
                )

                var savePatScoreEvent = getPatSaveScoreEvent(
                    eventItem = eventItem,
                    eventName = eventName,
                    eventType = eventType,
                    patScoreSaveEvent = requestPayloads,
                    prefRepo = prefRepo
                )

                val dependsOn = createEventDependency(eventItem, eventName, savePatScoreEvent)
                val metadata = savePatScoreEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                savePatScoreEvent = savePatScoreEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return savePatScoreEvent
            }

            EventName.SAVE_VO_ENDORSEMENT -> {
                val didiEntity = (eventItem as DidiEntity)
                val selectedTolaEntity = tolaDao.fetchSingleTolaFromServerId(didiEntity.cohortId)


                requestPayload = EditDidiWealthRankingRequest.getRequestPayloadForVoEndorsement(
                    eventItem,
                    tolaDeviceId = selectedTolaEntity?.localUniqueId ?: "",
                    tolaServerId = selectedTolaEntity?.serverId ?: 0
                ).json()
                payloadLocalId = eventItem.localUniqueId
            }

            EventName.SAVE_BPC_MATCH_SCORE -> {
                requestPayload = (eventItem as SaveMatchSummaryRequest).json()

            }

            else -> {
                return null
            }
        }
        var event = Events(
            name = eventName.name,
            type = eventName.topicName,
            createdBy = prefRepo.getUserId(),
            mobile_number = prefRepo.getMobileNumber(),
            modified_date = System.currentTimeMillis().toDate(),
            request_payload = requestPayload,
            payloadLocalId = payloadLocalId,
            status = EventSyncStatus.OPEN.eventSyncStatus,
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = emptyList(),
                request_payload_size = requestPayload.getSizeInLong(),
            ).json()
        )
        val dependsOn = createEventDependency(eventItem, eventName, event)
        val metadata = event.metadata?.getMetaDataDtoFromString()
        val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
        event = event.copy(
            metadata = updatedMetaData?.json()
        )
        return event
    }

    override suspend fun <T> createEventDependency(
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
                EventName.ADD_DIDI -> {
                    filteredList = eventList.filter {
                        val eventPayload =
                            dependentEvent.request_payload?.getAddDidiRequestPayloadFromString()
                        it.payloadLocalId == eventPayload?.cohortDeviceId
                    }
                }

                EventName.SAVE_WEALTH_RANKING, EventName.SAVE_PAT_ANSWERS, EventName.REJECTED_PAT_SCORE, EventName.INPROGRESS_PAT_SCORE, EventName.COMPLETED_PAT_SCORE, EventName.NOT_AVAILBLE_PAT_SCORE, EventName.SAVE_VO_ENDORSEMENT, EventName.CRP_IMAGE, EventName.BPC_IMAGE -> {
                    filteredList = eventList.filter {
                        if (prefRepo.isUserBPC()) {
                            var editRequest = Gson().fromJson(
                                it.request_payload,
                                EditDidiWealthRankingRequest::class.java
                            )
                            dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                                ?.get(KEY_PARENT_ENTITY_DIDI_NAME)?.equals(editRequest.name, true)!!
                                    && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                                ?.get(KEY_PARENT_ENTITY_DADA_NAME)
                                ?.equals(editRequest.guardianName, true)!!
                                    && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                                ?.get(KEY_PARENT_ENTITY_ADDRESS).equals(editRequest.address, true)
                                    && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                                ?.get(KEY_PARENT_ENTITY_TOLA_NAME)
                                ?.equals(editRequest.cohortName, true)!!
                        } else {
                            it.payloadLocalId == dependentEvent.payloadLocalId
                        }
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

    fun updateWorkflowId(stepId: Int, workflowId: Int, villageId: Int, status: String) {
        this.stepsListDao.updateWorkflowId(stepId, workflowId, villageId, status)
    }

    fun updateWorkflowId(stepId: Int, workflowId: Int, status: String) {
        this.stepsListDao.updateWorkflowId(stepId, workflowId, status)
    }

}

