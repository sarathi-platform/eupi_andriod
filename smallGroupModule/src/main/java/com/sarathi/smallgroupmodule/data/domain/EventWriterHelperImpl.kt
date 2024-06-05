package com.sarathi.smallgroupmodule.data.domain

import android.net.Uri
import com.nudge.core.BLANK_STRING
import com.nudge.core.Core
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
import com.nudge.core.enums.PayloadType
import com.nudge.core.enums.SubjectType
import com.nudge.core.eventswriter.EventWriterFactory
import com.nudge.core.eventswriter.IEventFormatter
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.nudge.syncmanager.EventWriterHelper
import com.nudge.syncmanager.getParentEntityMapForEvent
import com.nudge.syncmanager.model.PayloadData
import com.nudge.syncmanager.model.SaveAttendanceEventDto
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.smallgroupmodule.utils.getAttendanceFromBoolean
import com.sarathi.smallgroupmodule.utils.getDate
import javax.inject.Inject

class EventWriterHelperImpl @Inject constructor(
//    @ApplicationContext private val context: Context,
    val coreSharedPrefs: CoreSharedPrefs,
    private val eventsDao: EventsDao,
    private val eventDependencyDao: EventDependencyDao,
    private val subjectEntityDao: SubjectEntityDao,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao
) : EventWriterHelper {

    val context = Core.getContext()

    override suspend fun <T> createEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ): Events {
        if (eventType != EventType.STATEFUL)
            return Events.getEmptyEvent()

        when (eventName) {
            EventName.SAVE_SUBJECT_ATTENDANCE_EVENT -> {
                val requestPayload = (eventItem as SaveAttendanceEventDto)

                var event = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = coreSharedPrefs.getUserId(),
                    mobile_number = coreSharedPrefs.getMobileNo() ?: BLANK_STRING,
                    request_payload = requestPayload.json(),
                    status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    result = null,
                    consumer_status = BLANK_STRING,
                    payloadLocalId = BLANK_STRING,
                    metadata = MetadataDto(
                        mission = "SMALL_GROUP_ATTENDANCE" ?: BLANK_STRING,
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
            CoreLogger.e(context!!, "saveEventToMultipleSources", exception.message ?: "")
        }
    }

    override suspend fun <T> createEventDependency(
        eventItem: T,
        eventName: EventName,
        dependentEvent: Events
    ): List<EventDependencyEntity> {
        return emptyList()
    }

    override fun getEventFormatter(): IEventFormatter {
        return EventWriterFactory().createEventWriter(
            context!!,
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
                    EventWriterName.FILE_EVENT_WRITER,
                    EventWriterName.IMAGE_EVENT_WRITER,
                    EventWriterName.DB_EVENT_WRITER,
                    EventWriterName.LOG_EVENT_WRITER
                ), uri
            )
        } catch (exception: Exception) {
            CoreLogger.e(context!!, "ImageEventWriter", exception.message ?: "")
        }
    }

    override fun getBaseLineUserId(): String {
        return coreSharedPrefs.getUniqueUserIdentifier()
    }

    suspend fun createAttendanceEvent(
        subjectEntity: SubjectEntity,
        smallGroupSubTabUiModel: SmallGroupSubTabUiModel,
        attendance: Boolean,
        date: Long
    ): Events {

        val payloadData = listOf<PayloadData>(
            PayloadData(
                date = date.getDate(),
                id = smallGroupSubTabUiModel.smallGroupId.toString(),
                value = attendance.getAttendanceFromBoolean()
            )
        )

        val saveAttendanceEventDto = SaveAttendanceEventDto(
            dateCreated = System.currentTimeMillis(),
            languageId = coreSharedPrefs.getSelectedLanguageId(),
            subjectId = subjectEntity.subjectId.value(),
            subjectType = SubjectType.SUBJECT_TYPE_DIDI.name,
            tagId = 94,
            payloadType = PayloadType.PAYLOAD_TYPE_ATTENDANCE.name,
            payloadData = payloadData
        )

        return createEvent(
            saveAttendanceEventDto,
            EventName.SAVE_SUBJECT_ATTENDANCE_EVENT,
            EventType.STATEFUL
        )

    }


}