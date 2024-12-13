package com.sarathi.dataloadingmangement.repository

import android.content.Context
import android.net.Uri
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
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
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.dao.revamp.LivelihoodConfigEntityDao
import com.sarathi.dataloadingmangement.data.dao.revamp.MissionConfigEntityDao
import com.sarathi.dataloadingmangement.model.events.DeleteAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.LivelihoodPlanActivityEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerMoneyJorunalEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAttendanceEventDto
import com.sarathi.dataloadingmangement.model.events.SaveDocumentEventDto
import com.sarathi.dataloadingmangement.model.events.SaveFormAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.SectionStatusUpdateEventDto
import com.sarathi.dataloadingmangement.model.events.TrainingTypeActivitySaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateActivityStatusEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateMissionStatusEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateTaskStatusEventDto
import com.sarathi.dataloadingmangement.model.events.incomeExpense.DeleteLivelihoodEvent
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveAssetJournalEventDto
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveLivelihoodEventDto
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveMoneyJournalEventDto
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject

class EventWriterRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventsDao: EventsDao,
    private val eventDependencyDao: EventDependencyDao,
    val coreSharedPrefs: CoreSharedPrefs,
    private val eventStatusDao: EventStatusDao,
    private val imageStatusDao: ImageStatusDao,
    private val missionConfigEntityDao: MissionConfigEntityDao,
    private val livelihoodConfigEntityDao: LivelihoodConfigEntityDao
) :
    IEventWriterRepository {
    override suspend fun <T> createAndSaveEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType,
        surveyName: String,
        isFromRegenerate: Boolean

    ): Events? {

        if (eventType != EventType.STATEFUL)
            return Events.getEmptyEvent()
        var requestPayload = ""

        when (eventName) {

            EventName.GRANT_SAVE_RESPONSE_EVENT -> {
                requestPayload = when (eventItem) {
                    is SaveAnswerEventDto -> {
                        (eventItem as SaveAnswerEventDto).json()
                    }

                    is TrainingTypeActivitySaveAnswerEventDto -> {
                        (eventItem as TrainingTypeActivitySaveAnswerEventDto).json()
                    }

                    else -> {
                        (eventItem as SaveAnswerEventDto).json()
                    }
                }

            }

            EventName.MONEY_JOURNAL_EVENT, EventName.FORM_RESPONSE_EVENT -> {
                requestPayload = (eventItem as SaveAnswerMoneyJorunalEventDto).json()

            }

            EventName.GRANT_DELETE_RESPONSE_EVENT -> {
                requestPayload = (eventItem as DeleteAnswerEventDto).json()

            }

            EventName.TASKS_STATUS_EVENT -> {
                requestPayload = (eventItem as UpdateTaskStatusEventDto).json()
            }

            EventName.ACTIVITIES_STATUS_EVENT -> {
                requestPayload = (eventItem as UpdateActivityStatusEventDto).json()
            }

            EventName.MISSIONS_STATUS_EVENT -> {
                requestPayload = (eventItem as UpdateMissionStatusEventDto).json()

            }

            EventName.UPDATE_FORM_DETAILS_EVENT -> {
                requestPayload = (eventItem as SaveFormAnswerEventDto).json()

            }

            EventName.UPLOAD_DOCUMENT_EVENT -> {
                requestPayload = (eventItem as SaveDocumentEventDto).json()

            }

            EventName.ADD_SECTION_PROGRESS_FOR_DIDI_EVENT,
            EventName.UPDATE_SECTION_PROGRESS_FOR_DIDI_EVENT -> {

                requestPayload = (eventItem as SectionStatusUpdateEventDto).json()
            }

            EventName.SAVE_SUBJECT_ATTENDANCE_EVENT,
            EventName.DELETE_SUBJECT_ATTENDANCE_EVENT -> {
                requestPayload = (eventItem as SaveAttendanceEventDto).json()
            }
            EventName.LIVELIHOOD_OPTIONS_EVENT ->{
                requestPayload = (eventItem as LivelihoodPlanActivityEventDto).json()
            }

            EventName.MONEY_JOURNAL_RESPONSE_EVENT -> {
                requestPayload = (eventItem as SaveMoneyJournalEventDto).json()
            }

            EventName.ASSET_JOURNAL_EVENT -> {
                requestPayload = (eventItem as SaveAssetJournalEventDto).json()
            }

            EventName.LIVELIHOOD_EVENT -> {
                requestPayload = (eventItem as SaveLivelihoodEventDto).json()
            }

            EventName.DELETE_RESPONSE_EVENT -> {
                requestPayload = (eventItem as DeleteLivelihoodEvent).json()
            }


            else -> {
                requestPayload = BLANK_STRING
            }


        }
        var event = Events(
            name = eventName.name,
            type = eventName.topicName,
            createdBy = coreSharedPrefs.getUserName(),
            mobile_number = coreSharedPrefs.getMobileNo(),
            request_payload = requestPayload,
            status = EventSyncStatus.OPEN.eventSyncStatus,
            modified_date = System.currentTimeMillis().toDate(),
            payloadLocalId = BLANK_STRING,
            metadata = MetadataDto(
                isRegenerateFile = isFromRegenerate,
                mission = surveyName,
                depends_on = listOf(),
                request_payload_size = requestPayload.json().getSizeInLong(),
                parentEntity = emptyMap()
            ).json()
        )
        event = applyMetaDataChanges(
            event = event,
            getMissionConfig(getMissionId(requestPayload = requestPayload))
        )
        return event
    }


    private fun applyMetaDataChanges(event: Events, dataMap: Map<String, Any>): Events {
        if (dataMap.isEmpty() || event.request_payload.isNullOrEmpty()) {
            return event
        }
        val metadata = event.metadata?.getMetaDataDtoFromString()?.let { metadataDto ->
            val updatedData = metadataDto.data.toMutableMap().apply {
                putAll(dataMap)
            }
            metadataDto.copy(data = updatedData)
        }
        return event.copy(metadata = metadata?.json())
    }

    private fun getMissionId(requestPayload: String): Int {
        val jsonObject = JSONObject(requestPayload)
        return jsonObject.optInt("missionId", 0)
    }

    private fun getMissionConfig(missionId: Int): Map<String, Any> {
        if (missionId == 0) return emptyMap()
        val userId = coreSharedPrefs.getUniqueUserIdentifier()
        val languageCode = coreSharedPrefs.getSelectedLanguageCode()
        val missionLivelihoodType = missionConfigEntityDao.getMissionConfigLivelihood(
            missionId = missionId, uniqueUserIdentifier = userId
        )
        val missionLivelihood = livelihoodConfigEntityDao.getLivelihoodConfigForMission(
            missionId = missionId, uniqueUserIdentifier = userId, language = languageCode
        )
        if (missionLivelihoodType.isNullOrEmpty() || missionLivelihood?.livelihoodType.isNullOrEmpty()) {
            return emptyMap()
        }
        return hashMapOf<String, Any>().apply {
            put("missionType", missionLivelihoodType)
            missionLivelihood?.let {
                put("livelihoodOrder", "${it.livelihoodOrder}")
                put("livelihoodType", it.livelihoodType)
            }
        }
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
            CoreLogger.e(context, "ImageEventWriter", exception.message ?: "")
        }
    }


    override fun getEventFormatter(): IEventFormatter {
        return EventWriterFactory().createEventWriter(
            context,
            EventFormatterName.JSON_FORMAT_EVENT,
            eventsDao = eventsDao,
            eventDependencyDao = eventDependencyDao,
            eventStatusDao = eventStatusDao,
            imageStatusDao = imageStatusDao
        )
    }

    override suspend fun saveImageEventToMultipleSources(event: Events, uri: Uri) {

        val eventFormatter: IEventFormatter = getEventFormatter()
        try {
            eventFormatter.saveAndFormatEvent(
                event = event,
                dependencyEntity = listOf(),
                listOf(
                    EventWriterName.IMAGE_EVENT_WRITER, EventWriterName.DB_EVENT_WRITER
                ), uri
            )
        } catch (exception: Exception) {
            CoreLogger.e(context, "ImageEventWriter", exception.message ?: "")
        }

    }

}