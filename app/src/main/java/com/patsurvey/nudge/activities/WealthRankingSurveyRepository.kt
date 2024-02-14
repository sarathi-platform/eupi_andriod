package com.patsurvey.nudge.activities

import com.google.gson.Gson
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
import com.nudge.core.getEventDependencyEntityListFromEvents
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.toDate
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.activities.settings.TransactionIdResponse
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.getParentEntityMapForEvent
import javax.inject.Inject

class WealthRankingSurveyRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao,
    val eventsDao: EventsDao,
    val apiService: ApiService
):BaseRepository() {

    fun getAllDidisForVillage(villageId:Int): List<DidiEntity>{
       return didiDao.getAllDidisForVillage(villageId)
    }

    fun getStepForVillage(villageId: Int,stepId:Int): StepListEntity {
       return stepsListDao.getStepForVillage(villageId, stepId)
    }

    fun getAllStepsForVillage(villageId:Int) : List<StepListEntity>{
      return stepsListDao.getAllStepsForVillage(villageId)
    }
    suspend fun editWorkFlow(addWorkFlowRequest: List<EditWorkFlowRequest>): ApiResponseModel<List<WorkFlowResponse>> {
        NudgeLogger.d("WealthRankingSurveyRepository","editWorkFlow Request=> ${Gson().toJson(addWorkFlowRequest)}")
        return apiService.editWorkFlow(addWorkFlowRequest)
    }
    fun updateWorkflowId(stepId: Int, workflowId: Int,villageId:Int,status:String){
        stepsListDao.updateWorkflowId(
            stepId,
            workflowId,
            villageId,
            status
        )
    }

    fun updateNeedToPost(stepId:Int, villageId: Int, needsToPost: Boolean){
        stepsListDao.updateNeedToPost(stepId,villageId, needsToPost)
    }

    fun getVillage(villageId: Int): VillageEntity {
       return villageListDao.getVillage(villageId)
    }
    fun updateLastCompleteStep(villageId: Int, stepId: List<Int>){
        villageListDao.updateLastCompleteStep(villageId, stepId)
    }

    fun markStepAsCompleteOrInProgress(stepId: Int, isComplete: Int = 0,villageId:Int){
        stepsListDao.markStepAsCompleteOrInProgress(
            stepId,
            isComplete,
            villageId
        )
    }

    fun getAllSteps(): List<StepListEntity>{
        return stepsListDao.getAllSteps()
    }

    fun markStepAsInProgress(orderNumber: Int, inProgress: Int = 1,villageId:Int){
        stepsListDao.markStepAsInProgress(
            orderNumber,
            inProgress,
            villageId
        )
    }
    fun isStepComplete(stepId: Int): Int{
        return stepsListDao.isStepComplete(stepId, prefRepo.getSelectedVillage().id)
    }
    fun getAllNeedToPostDidiRanking(needsToPostRanking: Boolean): List<DidiEntity>{
        return didiDao.getAllNeedToPostDidiRanking(needsToPostRanking, 0)
    }
    suspend fun updateDidiRanking(didiWealthRankingRequest: List<EditDidiWealthRankingRequest>): ApiResponseModel<List<DidiEntity>>{
        NudgeLogger.d("WealthRankingSurveyRepository","updateDidiRanking Request=> ${Gson().toJson(didiWealthRankingRequest)}")
        return apiService.updateDidiRanking(didiWealthRankingRequest)
    }

    fun setNeedToPostRanking(didiId:Int, needsToPostRanking: Boolean){
        didiDao.setNeedToPostRanking(didiId, needsToPostRanking)
    }

    fun updateDidiTransactionId(didiId: Int, transactionId: String){
        didiDao.updateDidiTransactionId(didiId,
            transactionId = transactionId
        )
    }

    fun fetchPendingWealthStatusDidi(needsToPostRanking: Boolean,transactionId : String?) : List<DidiEntity>{
       return didiDao.fetchPendingWealthStatusDidi(needsToPostRanking, transactionId)
    }

    suspend fun getPendingStatus(transactionIdRequest: TransactionIdRequest): ApiResponseModel<List<TransactionIdResponse>>{
        NudgeLogger.d("WealthRankingSurveyRepository","getPendingStatus Request=> ${Gson().toJson(transactionIdRequest)}")
        return apiService.getPendingStatus(transactionIdRequest)
    }

    fun updateRankEditFlag(villageId: Int, rankingEdit: Boolean){
        didiDao.updateRankEditFlag(villageId, rankingEdit = rankingEdit)
    }

    fun updateDidiNeedToPostWealthRank(didiId: Int, needsToPostRanking: Boolean){
        didiDao.updateDidiNeedToPostWealthRank(didiId, needsToPostRanking)
    }

    suspend fun updateWealthRankingInDb(didiId: Int, wealthRank: String) {
        didiDao.updateDidiRank(didiId, wealthRank)
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
            EventName.SAVE_WEALTH_RANKING -> {
                val requestPayload = EditDidiWealthRankingRequest
                    .getRequestPayloadForWealthRanking(didiEntity = (eventItem as DidiEntity))
                    .json()

                var saveWealthRankingEvent = Events(
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

                val dependsOn = createEventDependency(eventItem, eventName, saveWealthRankingEvent)
                val metadata = saveWealthRankingEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                saveWealthRankingEvent = saveWealthRankingEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return saveWealthRankingEvent
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
                EventName.SAVE_WEALTH_RANKING -> {
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

        eventDependencyList.addAll(filteredList.getEventDependencyEntityListFromEvents(dependentEvent))

        return eventDependencyList
    }

    override suspend fun <T> saveEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ) {
        val eventObserver = NudgeCore.getEventObserver()

        val event = this.createEvent(
            eventItem,
            eventName,
            eventType
        )

        if (event?.id?.equals(BLANK_STRING) != true) {
            event?.let {
                eventObserver?.addEvent(it)
                val eventDependencies = this.createEventDependency(eventItem, eventName, it)
                if (eventDependencies.isNotEmpty()) {
                    eventObserver?.addEventDependencies(eventDependencies)
                }
            }
        }
    }

}