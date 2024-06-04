package com.sarathi.dataloadingmangement.model.events.repository

import com.nudge.core.EventSyncStatus
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventDto
import javax.inject.Inject

class EventWriterRepositoryImpl @Inject constructor(
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


            EventName.UPDATE_TASK_STATUS_EVENT -> {
                requestPayload = (eventItem as UpdateTaskStatusEventDto)
            }

            EventName.UPDATE_ACTIVITY_STATUS_EVENT -> {
                requestPayload = (eventItem as UpdateActivityStatusEventDto)
            }

            EventName.UPDATE_MISSION_STATUS_EVENT -> {
                requestPayload = (eventItem as UpdateMissionStatusEventDto)

            }

            EventName.ADD_SECTION_PROGRESS_FOR_DIDI_EVENT,
            EventName.UPDATE_SECTION_PROGRESS_FOR_DIDI_EVENT -> {

                requestPayload = (eventItem as SectionStatusUpdateEventDto)
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

}