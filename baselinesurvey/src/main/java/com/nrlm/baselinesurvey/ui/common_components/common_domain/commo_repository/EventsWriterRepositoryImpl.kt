package com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository

import android.net.Uri
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventForFormQuestionDto
import com.nrlm.baselinesurvey.model.datamodel.SectionStatusUpdateEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateActivityStatusEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateMissionStatusEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateTaskStatusEventDto
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.getParentEntityMapForEvent
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
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.toDate
import javax.inject.Inject

class EventsWriterRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val surveyEntityDao: SurveyEntityDao,
    private val missionEntityDao: MissionEntityDao,
    private val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    private val eventsDao: EventsDao,
    private val eventDependencyDao: EventDependencyDao
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

                val mission = missionEntityDao.getMission(requestPayload.missionId)

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

                val mission = missionEntityDao.getMission(requestPayload.missionId)

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

                val mission = missionEntityDao.getMission(requestPayload.missionId)

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

}