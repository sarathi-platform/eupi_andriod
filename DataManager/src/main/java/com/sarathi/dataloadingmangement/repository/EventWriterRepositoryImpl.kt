package com.sarathi.dataloadingmangement.repository

import android.content.Context
import android.net.Uri
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
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
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerMoneyJorunalEventDto
import com.sarathi.dataloadingmangement.model.events.SectionStatusUpdateEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateActivityStatusEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateMissionStatusEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateTaskStatusEventDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class EventWriterRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventsDao: EventsDao,
    private val eventDependencyDao: EventDependencyDao,
    val coreSharedPrefs: CoreSharedPrefs
) :
    IEventWriterRepository {
    override suspend fun <T> createAndSaveEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType,
        surveyName: String,
    ): Events? {

        if (eventType != EventType.STATEFUL)
            return Events.getEmptyEvent()
        var requestPayload = ""

        when (eventName) {

            EventName.SAVE_RESPONSE_EVENT -> {
                requestPayload = (eventItem as SaveAnswerEventDto).json()

            }
            EventName.MONEY_JOURNAL_EVENT -> {
                requestPayload = (eventItem as SaveAnswerMoneyJorunalEventDto).json()

            }


            EventName.UPDATE_TASK_STATUS_EVENT -> {
                requestPayload = (eventItem as UpdateTaskStatusEventDto).json()
            }

            EventName.UPDATE_ACTIVITY_STATUS_EVENT -> {
                requestPayload = (eventItem as UpdateActivityStatusEventDto).json()
            }

            EventName.UPDATE_MISSION_STATUS_EVENT -> {
                requestPayload = (eventItem as UpdateMissionStatusEventDto).json()

            }

            EventName.ADD_SECTION_PROGRESS_FOR_DIDI_EVENT,
            EventName.UPDATE_SECTION_PROGRESS_FOR_DIDI_EVENT -> {

                requestPayload = (eventItem as SectionStatusUpdateEventDto).json()
            }

            else -> {
                requestPayload = ""
            }


        }
        val event = Events(
            name = eventName.name,
            type = eventName.topicName,
            createdBy = coreSharedPrefs.getUniqueUserIdentifier(),
            mobile_number = coreSharedPrefs.getMobileNo(),
            request_payload = requestPayload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            payloadLocalId = BLANK_STRING,
            metadata = MetadataDto(
                mission = surveyName,
                depends_on = listOf(),
                request_payload_size = requestPayload.json().getSizeInLong(),
                parentEntity = emptyMap()
            ).json()
        )

        return event
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
            eventDependencyDao
        )
    }

    override suspend fun saveImageEventToMultipleSources(event: Events, uri: Uri) {

        val eventFormatter: IEventFormatter = getEventFormatter()
        try {
            eventFormatter.saveAndFormatEvent(
                event = event,
                dependencyEntity = listOf(),
                listOf(
                    EventWriterName.IMAGE_EVENT_WRITER,
                ), uri
            )
        } catch (exception: Exception) {
            CoreLogger.e(context, "ImageEventWriter", exception.message ?: "")
        }

    }

}