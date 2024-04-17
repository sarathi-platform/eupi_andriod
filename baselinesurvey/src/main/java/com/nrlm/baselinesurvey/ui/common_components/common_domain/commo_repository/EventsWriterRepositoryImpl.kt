package com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository

import android.net.Uri
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.data.domain.EventWriterHelper
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventForFormQuestionDto
import com.nrlm.baselinesurvey.model.datamodel.SectionStatusUpdateEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateActivityStatusEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateMissionStatusEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateTaskStatusEventDto
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.convertInputTypeQuestionToEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertToSaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.getParentEntityMapForEvent
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.getDependentEventsId
import com.nudge.core.enums.EventFormatterName
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventswriter.EventWriterFactory
import com.nudge.core.eventswriter.IEventFormatter
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import javax.inject.Inject

class EventsWriterRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val surveyEntityDao: SurveyEntityDao,
    private val missionEntityDao: MissionEntityDao,
    private val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    private val eventsDao: EventsDao,
    private val eventDependencyDao: EventDependencyDao,
    private val baselineDatabase: NudgeBaselineDatabase,
    private val eventWriterHelper: EventWriterHelper
) : EventsWriterRepository {

    override suspend fun <T> createEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ): Events? {
        if (eventType != EventType.STATEFUL)
            return Events.getEmptyEvent()

        when (eventName) {
            EventName.ADD_SECTION_PROGRESS_FOR_DIDI_EVENT,
            EventName.UPDATE_SECTION_PROGRESS_FOR_DIDI_EVENT -> {

                val requestPayload = (eventItem as SectionStatusUpdateEventDto)
                val survey = surveyEntityDao.getSurveyDetailForLanguage(
                    requestPayload.surveyId,
                    prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
                )

                var event = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber() ?: BLANK_STRING,
                    request_payload = requestPayload.json(),
                    status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    result = null,
                    consumer_status = BLANK_STRING,
                    payloadLocalId = BLANK_STRING,
                    metadata = MetadataDto(
                        mission = survey?.surveyName ?: BLANK_STRING,
                        depends_on = listOf(),
                        request_payload_size = requestPayload.json().getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json()
                )
                if (eventName.depends_on.isNotEmpty()) {
                    val dependsOn = createEventDependency(eventItem, eventName, event)
                    val metadata = event.metadata?.getMetaDataDtoFromString()
                    val updatedMetaData =
                        metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                    event = event.copy(
                        metadata = updatedMetaData?.json()
                    )
                }

                return event
            }

            EventName.SAVE_RESPONSE_EVENT -> {
                when (eventItem) {
                    is SaveAnswerEventDto -> {
                        val requestPayload = (eventItem as SaveAnswerEventDto)
                        val survey = surveyEntityDao.getSurveyDetailForLanguage(
                            requestPayload.surveyId,
                            prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
                        )

                        var event = Events(
                            name = eventName.name,
                            type = eventName.topicName,
                            createdBy = prefRepo.getUserId(),
                            mobile_number = prefRepo.getMobileNumber() ?: BLANK_STRING,
                            request_payload = requestPayload.json(),
                            status = EventSyncStatus.OPEN.name,
                            modified_date = System.currentTimeMillis().toDate(),
                            result = null,
                            consumer_status = BLANK_STRING,
                            payloadLocalId = BLANK_STRING,
                            metadata = MetadataDto(
                                mission = survey?.surveyName ?: BLANK_STRING,
                                depends_on = listOf(),
                                request_payload_size = requestPayload.json().getSizeInLong(),
                                parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                            ).json()
                        )

                        if (eventName.depends_on.isNotEmpty()) {
                            val dependsOn = createEventDependency(eventItem, eventName, event)
                            val metadata = event.metadata?.getMetaDataDtoFromString()
                            val updatedMetaData =
                                metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                            event = event.copy(
                                metadata = updatedMetaData?.json()
                            )
                        }

                        return event
                    }

                    is SaveAnswerEventForFormQuestionDto -> {
                        val requestPayload = (eventItem as SaveAnswerEventForFormQuestionDto)
                        val survey = surveyEntityDao.getSurveyDetailForLanguage(
                            requestPayload.surveyId,
                            prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
                        )

                        var event = Events(
                            name = eventName.name,
                            type = eventName.topicName,
                            createdBy = prefRepo.getUserId(),
                            mobile_number = prefRepo.getMobileNumber() ?: BLANK_STRING,
                            request_payload = requestPayload.json(),
                            status = EventSyncStatus.OPEN.name,
                            modified_date = System.currentTimeMillis().toDate(),
                            result = null,
                            consumer_status = BLANK_STRING,
                            payloadLocalId = BLANK_STRING,
                            metadata = MetadataDto(
                                mission = survey?.surveyName ?: BLANK_STRING,
                                depends_on = listOf(),
                                request_payload_size = requestPayload.json().getSizeInLong(),
                                parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                            ).json()
                        )

                        if (eventName.depends_on.isNotEmpty()) {
                            val dependsOn = createEventDependency(eventItem, eventName, event)
                            val metadata = event.metadata?.getMetaDataDtoFromString()
                            val updatedMetaData =
                                metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                            event = event.copy(
                                metadata = updatedMetaData?.json()
                            )
                        }

//                        event = getDependsOnForEvent(eventItem, event, eventName)

                        return event
                    }

                    else -> {
                        return Events.getEmptyEvent()
                    }
                }
            }

            EventName.UPDATE_TASK_STATUS_EVENT -> {
                val requestPayload = (eventItem as UpdateTaskStatusEventDto)
                val mission =
                    missionEntityDao.getMission(userId = getUserId(), requestPayload.missionId)

                var event = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber() ?: BLANK_STRING,
                    request_payload = requestPayload.json(),
                    status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    result = null,
                    consumer_status = BLANK_STRING,
                    payloadLocalId = BLANK_STRING,
                    metadata = MetadataDto(
                        mission = mission.missionName ?: BLANK_STRING,
                        depends_on = listOf(),
                        request_payload_size = requestPayload.json().getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json()
                )

                if (eventName.depends_on.isNotEmpty()) {
                    val dependsOn = createEventDependency(eventItem, eventName, event)
                    val metadata = event.metadata?.getMetaDataDtoFromString()
                    val updatedMetaData =
                        metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                    event = event.copy(
                        metadata = updatedMetaData?.json()
                    )
                }

                return event
            }

            EventName.UPDATE_ACTIVITY_STATUS_EVENT -> {
                val requestPayload = (eventItem as UpdateActivityStatusEventDto)
                val mission = missionEntityDao.getMission(getUserId(), requestPayload.missionId)

                var event = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber() ?: BLANK_STRING,
                    request_payload = requestPayload.json(),
                    status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    result = null,
                    consumer_status = BLANK_STRING,
                    payloadLocalId = BLANK_STRING,
                    metadata = MetadataDto(
                        mission = mission.missionName ?: BLANK_STRING,
                        depends_on = listOf(),
                        request_payload_size = requestPayload.json().getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json()
                )

                if (eventName.depends_on.isNotEmpty()) {
                    val dependsOn = createEventDependency(eventItem, eventName, event)
                    val metadata = event.metadata?.getMetaDataDtoFromString()
                    val updatedMetaData =
                        metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                    event = event.copy(
                        metadata = updatedMetaData?.json()
                    )
                }

                return event
            }

            EventName.UPDATE_MISSION_STATUS_EVENT -> {
                val requestPayload = (eventItem as UpdateMissionStatusEventDto)
                val mission = missionEntityDao.getMission(getUserId(), requestPayload.missionId)

                var event = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber() ?: BLANK_STRING,
                    request_payload = requestPayload.json(),
                    status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    result = null,
                    consumer_status = BLANK_STRING,
                    payloadLocalId = BLANK_STRING,
                    metadata = MetadataDto(
                        mission = mission.missionName ?: BLANK_STRING,
                        depends_on = listOf(),
                        request_payload_size = requestPayload.json().getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json()
                )

                if (eventName.depends_on.isNotEmpty()) {
                    val dependsOn = createEventDependency(eventItem, eventName, event)
                    val metadata = event.metadata?.getMetaDataDtoFromString()
                    val updatedMetaData =
                        metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                    event = event.copy(
                        metadata = updatedMetaData?.json()
                    )
                }

                return event
            }

            else -> {
                return Events.getEmptyEvent()
            }
        }
    }

    override suspend fun <T> createEventDependency(
        eventItem: T,
        eventName: EventName,
        dependentEvent: Events
    ): List<EventDependencyEntity> {
        return emptyList()
    }


    override suspend fun saveEventToMultipleSources(
        event: Events,
        eventDependencies: List<EventDependencyEntity>,
        eventType: EventType
    ) {
        try {
            val selectedEventWriter = mutableListOf<EventWriterName>(
                EventWriterName.LOG_EVENT_WRITER
            )

            if (eventType == EventType.STATEFUL) {
                selectedEventWriter.add(EventWriterName.FILE_EVENT_WRITER)
                selectedEventWriter.add(EventWriterName.DB_EVENT_WRITER)
            }

            val eventFormatter: IEventFormatter = getEventFormatter()
            eventFormatter.saveAndFormatEvent(
                event = event,
                dependencyEntity = eventDependencies,
                selectedEventWriter
            )
        } catch (exception: Exception) {
            BaselineLogger.e("saveEventToMultipleSources", exception.message ?: "")
        }
    }


    override fun getEventFormatter(): IEventFormatter {
        return EventWriterFactory().createEventWriter(
            BaselineCore.getAppContext(),
            EventFormatterName.JSON_FORMAT_EVENT,
            eventsDao = eventsDao,
            eventDependencyDao
        )
    }

    override suspend fun isSectionProgressForDidiAlreadyAdded(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): Boolean {
        return didiSectionProgressEntityDao.getSectionProgressForDidi(
            userId = getUserId(),
            surveyId,
            sectionId,
            didiId
        ) == null
    }

    override suspend fun saveImageEventToMultipleSources(event: Events, uri: Uri) {

        val eventFormatter: IEventFormatter = getEventFormatter()
        try {
            eventFormatter.saveAndFormatEvent(
                event = event,
                dependencyEntity = listOf(),
                listOf(
                    EventWriterName.FILE_EVENT_WRITER,
                    EventWriterName.IMAGE_EVENT_WRITER,
                    EventWriterName.DB_EVENT_WRITER,
                    EventWriterName.LOG_EVENT_WRITER
                ), uri
            )
        } catch (exception: Exception) {
            BaselineLogger.e("ImageEventWriter", exception.message ?: "")
        }

    }

    override fun getUserId(): String {
        return prefRepo.getMobileNumber() ?: BLANK_STRING
    }

    override suspend fun regenerateAllEvent() {

        changeFileName("regenerate_")
        generateResponseEvent()
        regenerateMATStatusEvent()
        changeFileName("")

    }

    private fun changeFileName(prefix: String) {
        val coreSharedPrefs = CoreSharedPrefs.getInstance(BaselineCore.getAppContext())
        coreSharedPrefs.setBackupFileName(getDefaultBackUpFileName(prefix + prefRepo.getMobileNumber()))
        coreSharedPrefs.setImageBackupFileName(getDefaultImageBackUpFileName(prefix + prefRepo.getMobileNumber()))
    }

    private suspend fun generateResponseEvent() {
        baselineDatabase.inputTypeQuestionAnswerDao()
            .getAllInputTypeAnswersForQuestion(prefRepo.getUniqueUserId()).forEach {
                val tag = baselineDatabase.questionEntityDao()
                    .getQuestionTag(it.surveyId, it.sectionId, it.questionId)
                val optionList = baselineDatabase.optionItemDao()
                    .getSurveySectionQuestionOptions(it.sectionId, it.surveyId, it.questionId, 2)
                var optionItemEntityState = ArrayList<OptionItemEntityState>()
                optionList.forEach { optionItemEntity ->
                    optionItemEntityState.add(
                        OptionItemEntityState(
                            optionItemEntity.optionId,
                            optionItemEntity,
                            !optionItemEntity.conditional
                        )
                    )
                }
                saveEventToMultipleSources(
                    eventWriterHelper.createSaveAnswerEvent(
                        it.surveyId,
                        it.sectionId,
                        it.didiId,
                        it.questionId,
                        QuestionType.Input.name,
                        tag,
                        true,
                        listOf(it).convertInputTypeQuestionToEventOptionItemDto(
                            it.questionId,
                            QuestionType.Input,
                            optionItemEntityState
                        )
                    ),
                    listOf(), eventType = EventType.STATEFUL,
                )


            }

        baselineDatabase.sectionAnswerEntityDao().getAllAnswer(prefRepo.getUniqueUserId()).forEach {
            val tag = baselineDatabase.questionEntityDao()
                .getQuestionTag(it.surveyId, it.sectionId, it.questionId)
            val optionList = baselineDatabase.optionItemDao()
                .getSurveySectionQuestionOptions(it.sectionId, it.surveyId, it.questionId, 2)


            saveEventToMultipleSources(
                eventWriterHelper.createSaveAnswerEvent(
                    it.surveyId,
                    it.sectionId,
                    it.didiId,
                    it.questionId,
                    it.questionType,
                    tag,
                    true,
                    optionList.convertToSaveAnswerEventOptionItemDto(QuestionType.valueOf(it.questionType))
                ), eventType = EventType.STATEFUL, eventDependencies = listOf()
            )
        }
    }

    private suspend fun regenerateFromResponseEvent() {
        //Todo @anupam

        //       val formResponseList= baselineDatabase.formQuestionResponseDao().getAllFormResponses(prefRepo.getUniqueUserId())
//        val questionIdMap= formResponseList.map {
//            it.questionId
//        }.distinct()
//
//
//
//
//
//           formResponseList.groupBy { it.questionId }.forEach{formQuestionResponseEntity->
//
//               formQuestionResponseEntity
//               val tag = baselineDatabase.questionEntityDao().getQuestionTag(formQuestionResponseEntity[formQuestionResponseEntity.key].surveyId, formQuestionResponseEntity.sectionId, formQuestionResponseEntity.questionId)
//
//               eventWriterHelper.createSaveAnswerEventForFormTypeQuestion(formQuestionResponseEntity.surveyId,formQuestionResponseEntity.sectionId,formQuestionResponseEntity.didiId,formQuestionResponseEntity.questionId,QuestionType.Form.name,
//                   tag, )
//           }

    }

    private suspend fun regenerateDidiInfoResponseEvent() {
//Todo @anupam
    }

    private suspend fun regenerateImageUploadEvent() {
//Todo @anupam

    }

    private suspend fun regenerateMATStatusEvent() {
        val userID = prefRepo.getUniqueUserId()
        baselineDatabase.missionEntityDao().getMissions(userID).forEach { missionEntity ->


            val event = eventWriterHelper.createMissionStatusUpdateEvent(
                missionId = missionEntity.missionId,
                SectionStatus.valueOf(
                    SectionStatus.getSectionStatusNameFromOrdinal(
                        missionEntity.missionStatus
                    )
                )
            )

            saveEventToMultipleSources(
                event,
                eventDependencies = listOf(),
                eventType = EventType.STATEFUL
            )
        }
        baselineDatabase.missionActivityEntityDao().getAllActivities(userID).forEach {

            val event = eventWriterHelper.createActivityStatusUpdateEvent(
                missionId = it.missionId,
                activityId = it.activityId,
                status = SectionStatus.valueOf(SectionStatus.getSectionStatusNameFromOrdinal(it.activityStatus))

            )
            saveEventToMultipleSources(
                event,
                eventDependencies = listOf(),
                eventType = EventType.STATEFUL
            )


        }

        baselineDatabase.activityTaskEntityDao().getAllActivityTask(userID).forEach {

            val event = eventWriterHelper.createTaskStatusUpdateEvent(
                subjectId = it.subjectId,

                sectionStatus = SectionStatus.valueOf(it.status ?: "")

            )
            saveEventToMultipleSources(
                event,
                eventDependencies = listOf(),
                eventType = EventType.STATEFUL
            )

        }
        baselineDatabase.didiSectionProgressEntityDao()
            .getAllSectionProgress(prefRepo.getUniqueUserId()).forEach {
                val event = eventWriterHelper.createUpdateSectionStatusEvent(
                    it.surveyId,
                    it.sectionId,
                    it.didiId,
                    SectionStatus.valueOf(SectionStatus.getSectionStatusNameFromOrdinal(it.sectionStatus))
                )
                saveEventToMultipleSources(
                    event,
                    eventDependencies = listOf(),
                    eventType = EventType.STATEFUL
                )

            }
    }

}