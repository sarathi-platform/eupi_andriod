package com.patsurvey.nudge.activities.ui.bpc.bpc_didi_list_screens

import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.getDependentEventsId
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.enums.getDependsOnEventNameForEvent
import com.nudge.core.getEventDependencyEntityListFromEvents
import com.nudge.core.json
import com.nudge.core.model.getMetaDataDtoFromString
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.getPatScoreSaveEvent
import javax.inject.Inject

class BPCDidiListRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao
):BaseRepository() {

    fun getAllTolasForVillage(): List<TolaEntity>{
       return tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAllStepsForVillage(): List<StepListEntity>{
       return stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAllDidisForVillage(): List<DidiEntity>{
        return didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
    }

    fun updateQuesSectionStatus(didiId: Int, patSurveyProgress: Int){
        didiDao.updateQuesSectionStatus(
            didiId,
            patSurveyProgress
        )
    }

    fun updateNeedToPostPAT(needsToPostPAT: Boolean,didiId: Int){
        didiDao.updateNeedToPostPAT(needsToPostPAT, didiId, prefRepo.getSelectedVillage().id)
        if (this.prefRepo.isUserBPC()) {
            didiDao.updateNeedsToPostBPCProcessStatus(true, didiId)
        }
    }
    fun getAllPendingPATDidisCount(): Int{
       return didiDao.getAllPendingPATDidisCount(prefRepo.getSelectedVillage().id)
    }

    fun isStepComplete(stepId: Int,villageId: Int): Int{
       return stepsListDao.isStepComplete(stepId, villageId)
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

        if (eventItem !is DidiEntity)
            return super.createEvent(eventItem, eventName, eventType)

        when (eventName) {

            EventName.NOT_AVAILBLE_PAT_SCORE -> {
                val requestPayload = getPatScoreSaveEvent(
                    didiEntity = (eventItem as DidiEntity),
                    questionListDao = questionListDao,
                    prefRepo = prefRepo,
                    "", (eventItem as DidiEntity).cohortId
                )

                var savePatScoreEvent = getPatSaveScoreEvent(
                    eventItem = eventItem,
                    eventName = eventName,
                    eventType = eventType,
                    patScoreSaveEvent = requestPayload,
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

            else -> {
                return null
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
        var dependentEventsName = eventName.getDependsOnEventNameForEvent()
        for (dependsOnEvent in dependentEventsName) {
            val eventList = eventsDao.getAllEventsForEventName(dependsOnEvent.name)
            when (eventName) {
                EventName.NOT_AVAILBLE_PAT_SCORE -> {
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

}