package com.patsurvey.nudge.activities.ui.vo_endorsement

import com.nudge.core.EventSyncStatus
import com.nudge.core.KEY_PARENT_ENTITY_ADDRESS
import com.nudge.core.KEY_PARENT_ENTITY_DADA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_DIDI_NAME
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_NAME
import com.nudge.core.SELECTION_MISSION
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.getDependentEventsId
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.enums.getDependsOnEventNameForEvent
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.toDate
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.getParentEntityMapForEvent
import javax.inject.Inject

class VoEndorsementSummaryRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val answerDao: AnswerDao,
    val questionListDao: QuestionListDao,
    val stepsListDao: StepsListDao,
    val eventsDao: EventsDao
):BaseRepository() {
    fun getAllQuestionsForLanguage():List<QuestionEntity>{
        return questionListDao.getAllQuestionsForLanguage(prefRepo.getAppLanguageId()?:2)
    }

    fun fetchVOEndorseStatusDidi(): List<DidiEntity>{
       return didiDao.fetchVOEndorseSummaryStatusDidi(prefRepo.getSelectedVillage().id)
    }

    fun getAllStepsForVillage(): List<StepListEntity>{
       return stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAnswerForDidi(didiId:Int,actionType:String): List<SectionAnswerEntity>{
        return answerDao.getAnswerForDidi(didiId = didiId, actionType = actionType)
    }

    fun updateVOEndorsementStatus(villageId: Int,didiId:Int,status:Int){
        didiDao.updateVOEndorsementStatus(villageId = villageId, didiId, status)
    }

    fun updateNeedToPostVO(needsToPostVo: Boolean,didiId: Int,villageId: Int){
        didiDao.updateNeedToPostVO(
            didiId = didiId,
            needsToPostVo = needsToPostVo,
            villageId = villageId
        )
    }

    override suspend fun <T> saveEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ) {
        val eventObserver = NudgeCore.getEventObserver()

        val event = this.createEvent(eventItem, eventName, eventType)

        if (event?.id?.equals(BLANK_STRING) != true) {
            event?.let {
                eventObserver?.addEvent(it)
                val eventDependencies = this.createEventDependency(eventItem, eventName, it)
                if (eventDependencies.isNotEmpty()) {
                    eventObserver?.addEventDependencies(eventDependencies)
                }
                saveEventToMultipleSources(it)
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

        if (eventItem !is DidiEntity)
            return super.createEvent(eventItem, eventName, eventType)

        when(eventName) {
            EventName.SAVE_VO_ENDORSEMENT -> {
                val requestPayload = EditDidiWealthRankingRequest.getRequestPayloadForVoEndorsement(eventItem).json()

                var updateDidiEndorsementStatusEvent = Events(
                    name = eventName.name,
                    type = eventName.topicName,
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

                val dependsOn = createEventDependency(eventItem, eventName, updateDidiEndorsementStatusEvent)
                val metadata = updateDidiEndorsementStatusEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                updateDidiEndorsementStatusEvent = updateDidiEndorsementStatusEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return updateDidiEndorsementStatusEvent

            }
            else -> {
                return super.createEvent(eventItem, eventName, eventType)
            }
        }

    }

    override suspend fun <T> createEventDependency(
        eventItem: T,
        eventName: EventName,
        dependentEvent: Events
    ): List<EventDependencyEntity> {
        val eventDependencyList = mutableListOf<EventDependencyEntity>()
        var filteredList = listOf<Events>()

        eventName.getDependsOnEventNameForEvent().forEach { dependsOnEvent ->
            val eventList = eventsDao.getAllEventsForEventName(dependsOnEvent.name)
            when (eventName) {
                EventName.SAVE_VO_ENDORSEMENT -> {
                    filteredList = eventList.filter {
                        val eventPayload = (eventItem as DidiEntity)
                        dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_DIDI_NAME)?.equals(eventPayload.name, true)!!
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_DADA_NAME)?.equals(eventPayload.guardianName, true)!!
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_ADDRESS).equals(eventPayload.address, true)
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_TOLA_NAME)?.equals(eventPayload.cohortName, true)!!
                    }
                }
                else -> {
                    filteredList = emptyList()
                }
            }
        }



        return eventDependencyList
    }
}